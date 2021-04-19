<template>
  <div>

    <login-required
        v-if="!isLoggedIn"
        page="view this business's Product Catalogue"
    />

    <admin-required
        v-else-if="!isAdminOf()"
        page="view this business's product catalogue"
    />

    <div v-else class="container-fluid">
      <div class="row justify-content-center">
        <!-- Page Content -->
        <div class="col-12 col-xl-10">

          <!--    Product Catalogue Header    -->
          <div class="row">
            <div class="col"/>
            <div class="col text-center">
              <h4>Product Catalogue</h4>
            </div>
            <div class="col text-right">
              <button class="btn btn-primary" v-on:click="newProduct">
                New Product
              </button>
            </div>
          </div>

          <!--    Error Alert    -->
          <div v-if="error" class="row">
            <div class="col text-center">
              <alert>{{ error }}</alert>
            </div>
          </div>

          <!--    Result Information    -->
          <div class="row">
            <div class="col">
              <div class="text-center">
                <showing-results-text
                    :items-per-page="resultsPerPage"
                    :page="page"
                    :total-count="totalCount"
                />
              </div>

              <!--    Order By   -->
              <div class="overflow-auto">
                <table class="table table-hover">
                  <thead>
                  <tr>
                    <!--    Product Code    -->
                    <th class="pointer" scope="col" @click="orderResults('id')">
                      <p class="d-inline">Code</p>
                      <p v-if="orderCol === 'id'" class="d-inline">{{ orderDirArrow }}</p>
                    </th>

                    <!--    Full Name    -->
                    <th class="pointer" scope="col" @click="orderResults('name')">
                      <p class="d-inline">Name</p>
                      <p v-if="orderCol === 'name'" class="d-inline">{{ orderDirArrow }}</p>
                    </th>

                    <!--    Description    -->
                    <th class="pointer" scope="col" @click="orderResults('description')">
                      <p class="d-inline">Description</p>
                      <p v-if="orderCol === 'description'" class="d-inline">{{ orderDirArrow }}</p>
                    </th>

                    <!--    Manufacturer    -->
                    <th class="pointer" scope="col" @click="orderResults('manufacturer')">
                      <p class="d-inline">Manufacturer</p>
                      <p v-if="orderCol === 'manufacturer'" class="d-inline">{{ orderDirArrow }}</p>
                    </th>

                    <!--    RRP    -->
                    <th class="pointer" scope="col" @click="orderResults('recommendedRetailPrice')">
                      <p class="d-inline">RRP</p>
                      <p v-if="orderCol === 'recommendedRetailPrice'" class="d-inline">{{ orderDirArrow }}</p>
                    </th>

                    <!--    Date Added    -->
                    <th class="pointer" scope="col" @click="orderResults('created')">
                      <p class="d-inline">Date Added</p>
                      <p v-if="orderCol === 'created'" class="d-inline">{{ orderDirArrow }}</p>
                    </th>

                    <!--    Edit button column    -->
                    <th scope="col"></th>
                  </tr>
                  </thead>
                  <!--    Product Information    -->
                  <tbody v-if="!loading">
                  <tr v-for="product in paginatedProducts"
                      v-bind:key="product.id"
                  >
                    <th scope="row">{{ product.id }}</th>
                    <td>{{ product.name }}</td>
                    <td style="word-wrap: break-word; width: 40%">{{ product.description }}</td>
                    <td>{{ product.manufacturer }}</td>
                    <td>{{ product.recommendedRetailPrice.toFixed(2) }}</td>
                    <td>{{ new Date(product.created).toDateString() }}</td>
                    <td style="color: blue; cursor: pointer;"
                        @click="editProduct(product.id)">
                      Edit
                    </td>
                  </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>

          <div v-if="loading" class="row">
            <div class="col text-center">
              <p class="text-muted">Loading...</p>
            </div>
          </div>

          <!--    Result Information    -->
          <div class="row">
            <div class="col">
              <pagination
                  :current-page.sync="page"
                  :items-per-page="resultsPerPage"
                  :total-items="totalCount"
                  class="mx-auto"
              />
            </div>
          </div>

        </div>
      </div>
    </div>

  </div>
</template>

<script>
import LoginRequired from './LoginRequired'
import AdminRequired from "@/components/AdminRequired";
import ShowingResultsText from "./ShowingResultsText";
import Pagination from "./Pagination";
import Alert from './Alert'
import {Business} from '@/Api';

export default {
  name: "Catalogue",
  components: {
    LoginRequired,
    AdminRequired,
    Alert,
    ShowingResultsText,
    Pagination
  },
  data() {
    return {
      products: [],
      error: null,
      orderCol: null,
      orderDirection: false, // False -> Ascending
      resultsPerPage: 10,
      page: 1,
      loading: false
    }
  },
  mounted() {
    this.fillTable()
  },

  computed: {
    /**
     * Gets the business ID
     * @returns {any}
     */
    businessId() {
      return this.$route.params.businessId;
    },

    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /**
     * Checks which direction (ascending or descending) the order by should be
     * @returns {string}
     */
    orderDirArrow() {
      if (this.orderDirection) {
        return '↓'
      }
      return '↑'
    },

    /**
     * Sort Products Logic
     * @returns {[]|*[]}
     */
    sortedProducts() {
      if (this.orderCol === null) {
        return this.products
      }

      // Create new products array and sort
      let newProducts = [...this.products];
      // Order direction multiplier for sorting
      const orderDir = (this.orderDirection ? 1 : -1);

      // Sort products if there are any
      if (newProducts.length > 0) {
        // Sort products
        newProducts.sort((a, b) => {
          return orderDir * this.sortAlpha(a, b)
        });
      }

      return newProducts
    },

    /**
     * Paginate the products
     * @returns {*[]|*[]}
     */
    paginatedProducts() {
      let newProducts = this.sortedProducts;

      // Sort products if there are any
      if (newProducts.length > 0) {
        // Splice the results to showing size
        const startIndex = this.resultsPerPage * (this.page - 1);
        const endIndex = this.resultsPerPage * this.page;
        newProducts = newProducts.slice(startIndex, endIndex)
      }

      return newProducts
    },
    /**
     * Calculates the number of results in products array
     * @returns {number}
     */
    totalCount() {
      return this.products.length
    }
  },
  methods: {
    /**
     * Check if the user is an admin of the business and is acting as that business
     */
    isAdminOf() {
      if (this.$root.$data.user.state.actingAs.type !== "business") return false
      return this.$root.$data.user.state.actingAs.id === parseInt(this.$route.params.businessId);
    },

    /**
     * Updates order direction
     * @param col column to be ordered
     */
    orderResults(col) {
      // Remove the ordering if the column is clicked and the arrow is down
      if (this.orderCol === col && this.orderDirection) {
        this.orderCol = null;
        this.orderDirection = false;
        return
      }

      // Updated order direction if the new column is the same as what is currently clicked
      this.orderDirection = this.orderCol === col;
      this.orderCol = col;
    },
    /**
     * Function for sorting a list by orderCol alphabetically
     */
    sortAlpha(a, b) {
      if (a[this.orderCol] === null) {
        return -1
      }
      if (b[this.orderCol] === null) {
        return 1
      }
      if (a[this.orderCol] < b[[this.orderCol]]) {
        return 1;
      }
      if (a[this.orderCol] > b[[this.orderCol]]) {
        return -1;
      }
      return 0;
    },
    /**
     * routes to the edit product page
     * @param id of the product
     */
    editProduct(id) {
      this.$router.push({name: 'editProduct', params: {businessId: this.businessId, productId: id}})
    },

    /**
     * Fills the table with Product data
     */
    fillTable() {
      this.products = [];
      this.loading = true;
      this.page = 1;

      Business.getProducts(this.$route.params.businessId)
          .then((res) => {
            this.error = null;
            this.products = res.data;
            this.loading = false;
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })
    },
    /**
     * Takes user to page to create new product.
     */
    newProduct() {
      this.$router.push({name: 'createProduct', params: {businessId: this.businessId}})
    }
  }
}
</script>

<style scoped>

.pointer {
  cursor: pointer;
}

</style>