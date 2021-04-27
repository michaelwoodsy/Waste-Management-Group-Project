<template>
  <div>
    <login-required
        v-if="!isLoggedIn"
        page="view an inventory"
    />

    <admin-required
        v-else-if="!isAdminOf()"
        page="view this business's product catalogue"
    />

    <div v-else class="container-fluid">
      <div class="row justify-content-center">
        <!-- Page Content -->
        <div class="col-12 col-xl-10">


          <!--    Inventory Header    -->
          <div class="row">
            <div class="col"/>
            <div class="col text-center">
              <h4>Inventory</h4>
            </div>
            <div class="col text-right">
              <button class="btn btn-primary" v-on:click="newInventoryItem">
                New Item
              </button>
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

                    <!--    Quantity    -->
                    <th scope="col" class="pointer" @click="orderResults('quantity')">
                      <p class="d-inline">Quantity</p>
                      <p class="d-inline" v-if="orderCol === 'quantity'">{{ orderDirArrow }}</p>
                    </th>

                    <!--    Price per Item    -->
                    <th scope="col" class="pointer" @click="orderResults('pricePerItem')">
                      <p class="d-inline">Price per Item</p>
                      <p class="d-inline" v-if="orderCol === 'pricePerItem'">{{ orderDirArrow }}</p>
                    </th>

                    <!--    Total Price    -->
                    <th scope="col" class="pointer" @click="orderResults('totalPrice')">
                      <p class="d-inline">Total Price</p>
                      <p class="d-inline" v-if="orderCol === 'totalPrice'">{{ orderDirArrow }}</p>
                    </th>

                    <!--   Manufactured date    -->
                    <th scope="col"  class="pointer" @click="orderResults('manufactured')">
                      <p class="d-inline">Manufactured Date</p>
                      <p class="d-inline" v-if="orderCol === 'manufactured'">{{ orderDirArrow }}</p>
                    </th>

                    <!--   Sell By date    -->
                    <th scope="col"  class="pointer" @click="orderResults('sellBy')">
                      <p class="d-inline">Sell By Date</p>
                      <p class="d-inline" v-if="orderCol === 'sellBy'">{{ orderDirArrow }}</p>
                    </th>

                    <!--   Best Before date    -->
                    <th scope="col"  class="pointer" @click="orderResults('bestBefore')">
                      <p class="d-inline">Best Before Date</p>
                      <p class="d-inline" v-if="orderCol === 'bestBefore'">{{ orderDirArrow }}</p>
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
                    <td>{{ item.quantity }}</td>
                    <td>{{ formatPrice(item.pricePerItem) }}</td>
                    <td>{{ formatPrice(item.totalPrice) }}</td>
                    <td>{{ formatDate(item.manufactured)}}</td>
                    <td>{{ formatDate(item.sellBy)}}</td>
                    <td>{{ formatDate(item.bestBefore)}}</td>
                    <td>{{ formatDate(item.expires)}}</td>
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
    </div>
  </div>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";
import Alert from "@/components/Alert";
import ShowingResultsText from "@/components/ShowingResultsText";
import Pagination from "@/components/Pagination";
import {Business} from "@/Api";

export default {
  name: "InventoryPage",
  components: {
    LoginRequired,
    AdminRequired,
    Alert,
    ShowingResultsText,
    Pagination
  },
  data () {
    return {
      inventoryItems: [],
      currency: null,
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
    this.getCurrency()
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
  watch: {
    /**
     * Called when the businessId is changed, this occurs when the path variable for the business id is updated
     */
    businessId() {
      this.fillTable()
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
     * Function for formatting inventory item dates
     */
    formatDate(dateString) {
      if (dateString === "" || dateString === null) {
        return "";
      } else {
        return new Date(dateString).toDateString();
      }
    },

    /**
     * calls the formatPrice method in the product module to format the products recommended retail price
     */
    formatPrice(price) {
      return this.$root.$data.product.formatPrice(this.currency, price)
    },

    /**
     * Takes user to page to create new product.
     */
    newInventoryItem() {
      this.$router.push({name: 'CreateInventoryItem', params: {businessId: this.businessId}})
    },

    /**
     * Uses the getCurrency in the product.js module to get the currency of the business
     */
    async getCurrency() {
      //Change country to businesses address country when implemented
      //The country variable  will always be an actual country as it is a requirement when creating a business
      const country = "Netherlands"

      this.currency = await this.$root.$data.product.getCurrency(country)
    },

    /**
     * Fills the table with Product data
     */
    fillTable() {
      this.inventoryItems = [];
      this.loading = true;
      this.page = 1;

      // TODO: uncomment when GET inventory implemented on backend
      Business.getInventory(this.$route.params.businessId)
          .then((res) => {
            this.error = null;
            this.inventoryItems = res.data;
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

</style>