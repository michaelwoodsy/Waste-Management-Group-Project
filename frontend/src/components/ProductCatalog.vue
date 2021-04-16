<template>
  <div>
    <div class="container-fluid" v-if="isLoggedIn">

      <!--    Search Users Header    -->
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h4>Product Catalog</h4>
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
                <th scope="col" class="pointer" @click="orderResults('code')">
                  <p class="d-inline">Code</p>
                  <p class="d-inline" v-if="orderCol === 'code'">{{ orderDirArrow }}</p>
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
                <th scope="col"  class="pointer" @click="orderResults('rrp')">
                  <p class="d-inline">RRP</p>
                  <p class="d-inline" v-if="orderCol === 'rrp'">{{ orderDirArrow }}</p>
                </th>

                <!--    Date Added    -->
                <th scope="col" class="pointer" @click="orderResults('dateAdded')">
                  <p class="d-inline">Date Added</p>
                  <p class="d-inline" v-if="orderCol === 'dateAdded'">{{ orderDirArrow }}</p>
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
                <!--TODO: Change variable names to actual names when product model is made-->
                <th scope="row">{{ product.id }}</th>
                <td>{{ product.name }}</td>
                <td>{{ product.manufacturer }}</td>
                <td>{{ product.recommendedRetailPrice }}</td>
                <td>{{ product.created }}</td>
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

    <login-required v-else page="view the catalog"/>
  </div>
</template>

<script>
import LoginRequired from './LoginRequired'
import ShowingResultsText from "./ShowingResultsText";
import Pagination from "./Pagination";
import Alert from './Alert'

export default {
  name: "Catalog",
  components: {
    LoginRequired,
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
    console.log(this.$route.params.businessId)
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

      // Sort users if there are any
      if (newProducts.length > 0) {
        // Sort users
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

      // Sort users if there are any
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
      this.$router.push({name: 'viewProduct', params: {productId: id}})
    },

    /**
     * Fills the table with Product data
     */
    fillTable() {
      this.products = [];
      this.loading = true;
      this.page = 1;

      this.products = this.fakeData()
      this.loading = false
      //TODO: Uncomment this when getProducts endpoint is implemented

      /*
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
       */
    },
    /**
     * Fills the table with fake data for now
     */
    fakeData() {
      return [
        {
          "id": "WATT-420-BEANS",
          "name": "Watties Baked Beans - 420g can",
          "description": "Baked Beans as they should be.",
          "recommendedRetailPrice": 2.2,
          "created": "2021-04-16T04:34:55.931Z",
          "images": []
        },
        {
          "id": "DORITO-300-CHEESE",
          "name": "Doritos Nacho Cheese - 300g",
          "description": "Gamer Fuel",
          "recommendedRetailPrice": 3.5,
          "created": "2021-04-16T04:35:43.931Z",
          "images": []
        },
      ]
    },
  }
}
</script>

<style scoped>

.pointer {
  cursor: pointer;
}

</style>
