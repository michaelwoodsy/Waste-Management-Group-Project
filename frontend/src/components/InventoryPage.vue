<template>
  <div>
    <!--TODO: test login/admin stuff here -->
<!--    <login-required-->
<!--        v-if="!isLoggedIn"-->
<!--        page="view an inventory"-->
<!--    />-->

<!--    <admin-required-->
<!--        v-else-if="!isAdminOf()"-->
<!--        page="view this business's product catalogue"-->
<!--    />-->

<!--    <div v-else>-->
    <div>

      <!--    Inventory Header    -->
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h4>Inventory</h4>
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
                <!--    Inventory Item Id    -->
                <th scope="col" class="pointer" @click="orderResults('id')">
                  <p class="d-inline">Id</p>
                  <p class="d-inline" v-if="orderCol === 'id'">{{ orderDirArrow }}</p>
                </th>
                <!--    Product Code    -->
                <th scope="col" class="pointer" @click="orderResults('productId')">
                  <p class="d-inline">Product Code</p>
                  <p class="d-inline" v-if="orderCol === 'productId'">{{ orderDirArrow }}</p>
                </th>

                <!--    Name    -->
                <th scope="col" class="pointer" @click="orderResults('name')">
                  <p class="d-inline">Name</p>
                  <p class="d-inline" v-if="orderCol === 'name'">{{ orderDirArrow }}</p>
                </th>

                <!--    Description    -->
                <th scope="col" class="pointer" @click="orderResults('description')">
                  <p class="d-inline">Description</p>
                  <p class="d-inline" v-if="orderCol === 'description'">{{ orderDirArrow }}</p>
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

                <!--   Expiry date    -->
                <th scope="col"  class="pointer" @click="orderResults('expires')">
                  <p class="d-inline">Expiry Date</p>
                  <p class="d-inline" v-if="orderCol === 'expires'">{{ orderDirArrow }}</p>
                </th>


              </tr>
              </thead>
              <!--    Product Information    -->
              <tbody v-if="!loading">
              <tr v-bind:key="item.id"
                  v-for="item in paginatedInventoryItems"
                  class="pointer"
              >
                <th scope="row">{{ item.id }}</th>
                <td>{{ item.product.id }}</td>
                <td>{{ item.product.name }}</td>
                <td>{{ item.product.description }}</td>
                <td>{{ item.product.manufacturer }}</td>
                <td>{{ item.product.recommendedRetailPrice }}</td>
                <td>{{ new Date(item.expires).toDateString() }}</td>
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
//TODO: uncomment these
// import LoginRequired from "@/components/LoginRequired";
// import AdminRequired from "@/components/AdminRequired";
import Alert from "@/components/Alert";
import ShowingResultsText from "@/components/ShowingResultsText";
import Pagination from "@/components/Pagination";
//import {Business} from "@/Api";

export default {
  name: "InventoryPage",
  components: {
    //TODO: uncomment these
    // LoginRequired,
    // AdminRequired,
    Alert,
    ShowingResultsText,
    Pagination
  },
  data () {
    return {
      inventoryItems: [],
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
     * Sort InventoryItems Logic
     * @returns {[]|*[]}
     */
    sortedInventoryItems () {
      if (this.orderCol === null) {
        return this.inventoryItems
      }

      // Create new inventory items array and sort
      let newInventoryItems = [...this.inventoryItems];
      // Order direction multiplier for sorting
      const orderDir = (this.orderDirection ? 1 : -1);

      // Sort inventory items if there are any
      if (newInventoryItems.length > 0) {
        // Sort inventory items
        newInventoryItems.sort((a, b) => {
          return orderDir * this.sortAlpha(a, b)
        });
      }

      return newInventoryItems
    },

    /**
     * Paginate the inventory items
     * @returns {*[]|*[]}
     */
    paginatedInventoryItems () {
      let newInventoryItems = this.sortedInventoryItems;

      // Sort inventory items if there are any
      if (newInventoryItems.length > 0) {
        // Splice the results to showing size
        const startIndex = this.resultsPerPage * (this.page - 1);
        const endIndex = this.resultsPerPage * this.page;
        newInventoryItems = newInventoryItems.slice(startIndex, endIndex)
      }

      return newInventoryItems
    },
    /**
     * Calculates the number of results in inventory items array
     * @returns {number}
     */
    totalCount () {
      return this.inventoryItems.length
    }
  },
  methods: {
    /**
     * Check if the user is an admin of the business and is acting as that business
     */
    isAdminOf() {
      return true;
      // if (this.$root.$data.user.state.actingAs.type !== "business") return false
      // return this.$root.$data.user.state.actingAs.id === parseInt(this.$route.params.businessId);
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
     * Fills the table with Product data
     */
    fillTable() {
      this.inventoryItems = [];
      this.loading = true;
      this.page = 1;

      this.inventoryItems = this.fakeData()
      this.loading = false

      // TODO: uncomment when GET inventory implemented on backend
      // Business.getInventory(this.$route.params.businessId)
      //     .then((res) => {
      //       this.error = null;
      //       this.inventoryItems = res.data;
      //       this.loading = false;
      //     })
      //     .catch((err) => {
      //       this.error = err;
      //       this.loading = false;
      //     })
    },
    fakeData () {
      return [
        {
          "id": 101,
          "product": {
            "id": "WATT-420g-BEANS",
            "name": "Watties Baked Beans - 420g can",
            "description": "Baked Beans as they should be.",
            "manufacturer": "Watties",
            "recommendedRetailPrice": 2.2,
            "created": "2021-04-16T04:34:55.931Z"
          },
          "quantity": 4,
          "pricePerItem": 2.2,
          "totalPrice": 8.8,
          "manufactured": "",
          "sellBy": "",
          "bestBefore": "",
          "expires": "2021-05-16T04:34:55.931Z"
        }, {
          "id": 102,
          "product": {
            "id": "DORITO-300-CHEESE",
            "name": "Doritos Nacho Cheese - 300g",
            "description": "Gamer Fuel",
            "manufacturer": "Doritoes inc.",
            "recommendedRetailPrice": 3.5,
            "created": "2021-04-16T04:34:55.931Z"
          },
          "quantity": 5,
          "pricePerItem": 3,
          "totalPrice": 15,
          "manufactured": "",
          "sellBy": "",
          "bestBefore": "",
          "expires": "2021-05-16T04:37:55.931Z"
        }
      ]
    }
  }
}
</script>


<style scoped>

</style>