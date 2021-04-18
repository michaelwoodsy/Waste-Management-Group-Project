<template>
  <div>
    <login-required
        v-if="!isLoggedIn"
        page="view an individual product"
    />

    <admin-required
        v-else-if="!isAdminOf()"
        page="view this business's product catalogue"
    />

    <div v-else>

      <!--    Product Catalogue Header    -->
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h4>Product Catalogue</h4>
        </div>
      </div>

      <!--    Error Alert    -->
      <div v-if="error" class="row">
        <div class="col-8 offset-2 text-center mb-2">
          <alert>{{ error }}</alert>
        </div>
      </div>

      <!--    Result Information    -->
      <div class="row">
        <div class="d-none d-lg-block col-lg-1"/>
        <div class="col-12 col-lg-10">
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
                <th scope="col" class="pointer" @click="orderResults('id')">
                  <p class="d-inline">Code</p>
                  <p class="d-inline" v-if="orderCol === 'id'">{{ orderDirArrow }}</p>
                </th>

                <!--    Full Name    -->
                <th scope="col" class="pointer" @click="orderResults('name')">
                  <p class="d-inline">Name</p>
                  <p class="d-inline" v-if="orderCol === 'name'">{{ orderDirArrow }}</p>
                </th>

                <!--    Manufacturer    -->
                <th scope="col" class="pointer" @click="orderResults('manufacturer')">
                  <p class="d-inline">Manufacturer</p>
                  <p class="d-inline" v-if="orderCol === 'manufacturer'">{{ orderDirArrow }}</p>
                </th>

                <!--    RRP    -->
                <th scope="col"  class="pointer" @click="orderResults('recommendedRetailPrice')">
                  <p class="d-inline">RRP</p>
                  <p class="d-inline" v-if="orderCol === 'recommendedRetailPrice'">{{ orderDirArrow }}</p>
                </th>

                <!--    Date Added    -->
                <th scope="col" class="pointer" @click="orderResults('created')">
                  <p class="d-inline">Date Added</p>
                  <p class="d-inline" v-if="orderCol === 'created'">{{ orderDirArrow }}</p>
                </th>
              </tr>
              </thead>
              <!--    Product Information    -->
              <tbody v-if="!loading">
              <tr v-bind:key="product.id"
                  v-for="product in paginatedProducts"
                  @click="viewProduct(product.id)"
                  class="pointer"
              >
                <th scope="row">{{ product.id }}</th>
                <td>{{ product.name }}</td>
                <td>{{ product.manufacturer }}</td>
                <td>{{ product.recommendedRetailPrice }}</td>
                <td>{{ new Date(product.created).toDateString() }}</td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="d-none d-lg-block col-lg-1"/>
      </div>

      <div class="row" v-if="loading">
        <div class="col-12 text-center">
          <p class="text-muted">Loading...</p>
        </div>
      </div>

      <!--    Result Information    -->
      <div class="row">
        <div class="col-12">
          <pagination
              :total-items="totalCount"
              :current-page.sync="page"
              :items-per-page="resultsPerPage"
              class="mx-auto"
          />
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
  data () {
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
    isLoggedIn () {
      return this.$root.$data.user.state.loggedIn
    },

    /**
     * Checks which direction (ascending or descending) the order by should be
     * @returns {string}
     */
    orderDirArrow () {
      if (this.orderDirection) {
        return '↓'
      }
      return '↑'
    },

    /**
     * Sort Products Logic
     * @returns {[]|*[]}
     */
    sortedProducts () {
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
    paginatedProducts () {
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
    totalCount () {
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
    orderResults (col) {
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
    sortAlpha (a, b) {
      if(a[this.orderCol] === null) { return -1 }
      if(b[this.orderCol] === null) { return 1 }
      if(a[this.orderCol] < b[[this.orderCol]]) { return 1; }
      if(a[this.orderCol] > b[[this.orderCol]]) { return -1; }
      return 0;
    },
    /**
     * routes to the individual product page
     * @param id of the product
     */
    viewProduct(id) {
      this.$router.push({name: 'individualProduct', params: {businessId:this.businessId ,productId: id}})
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
    }
  }
}
</script>

<style scoped>

.pointer {
  cursor: pointer;
}

</style>
