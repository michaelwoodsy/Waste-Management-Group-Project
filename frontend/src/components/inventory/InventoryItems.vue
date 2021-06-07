<template>
  <div class="container-fluid p-0">

    <!--    Result Information    -->
    <div v-if="!this.selectingItem || (!loading && this.inventoryItems.length > 0)">

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
            <th class="pointer" scope="col" @click="orderResults('productId')">
              <p class="d-inline">Product Code</p>
              <p v-if="orderCol === 'productId'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--    Product Image    -->
            <th></th>

            <!--    Quantity    -->
            <th class="pointer" scope="col" @click="orderResults('quantity')">
              <p v-if="!selectingItem" class="d-inline">Quantity</p>
              <p v-if="selectingItem" class="d-inline">Quantity Available</p>
              <p v-if="orderCol === 'quantity'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--    Price per Item    -->
            <th class="pointer" scope="col" @click="orderResults('pricePerItem')">
              <p class="d-inline">Price per Item</p>
              <p v-if="orderCol === 'pricePerItem'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--    Total Price    -->
            <th class="pointer" scope="col" @click="orderResults('totalPrice')">
              <p class="d-inline">Total Price</p>
              <p v-if="orderCol === 'totalPrice'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--   Manufactured date    -->
            <th class="pointer" scope="col" @click="orderResults('manufactured')">
              <p class="d-inline">Manufactured Date</p>
              <p v-if="orderCol === 'manufactured'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--   Sell By date    -->
            <th class="pointer" scope="col" @click="orderResults('sellBy')">
              <p class="d-inline">Sell By Date</p>
              <p v-if="orderCol === 'sellBy'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--   Best Before date    -->
            <th class="pointer" scope="col" @click="orderResults('bestBefore')">
              <p class="d-inline">Best Before Date</p>
              <p v-if="orderCol === 'bestBefore'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--   Expiry date    -->
            <th class="pointer" scope="col" @click="orderResults('expires')">
              <p class="d-inline">Expiry Date</p>
              <p v-if="orderCol === 'expires'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--    Edit button column    -->
            <th scope="col"></th>

          </tr>
          </thead>
          <!--    Inventory item Information    -->
          <tbody v-if="!loading">
          <tr v-for="item in paginatedInventoryItems"
              v-bind:key="item.id"
          >
            <td>{{ item.product.id }}</td>
            <td>
              <img alt="productImage" class="ui-icon-image"
                   src="@/../../media/defaults/defaultProduct_thumbnail.jpg">
            </td>
            <td>
              <span v-if="!selectingItem">{{ item.quantity }}</span>
              <span v-if="selectingItem">{{ getMaxQuantity(item) }}/{{ item.quantity }}</span>
            </td>
            <td>{{ formatPrice(item.pricePerItem) }}</td>
            <td>{{ formatPrice(item.totalPrice) }}</td>
            <td>{{ formatDate(item.manufactured) }}</td>
            <td>{{ formatDate(item.sellBy) }}</td>
            <td>{{ formatDate(item.bestBefore) }}</td>
            <td>{{ formatDate(item.expires) }}</td>
            <td v-if="!selectingItem" style="color: blue; cursor: pointer;"
                @click="editProduct(item.id)">
              Edit
            </td>
            <td v-if="selectingItem">
              <button class="btn btn-primary" @click="selectProduct(item.id)">Select</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div v-if="loading" class="row">
      <div class="col-12 text-center">
        <p class="text-muted">Loading...</p>
      </div>
    </div>

    <div v-if="!loading && this.inventoryItems.length === 0 && this.selectingItem" class="text-center">
      You have no inventory items to list for sale...
    </div>

    <!--    Result Information    -->
    <div class="row">
      <div class="col-12">
        <pagination
            :current-page.sync="page"
            :items-per-page="resultsPerPage"
            :total-items="totalCount"
            class="mx-auto"
        />
      </div>
    </div>

  </div>
</template>

<script>
import ShowingResultsText from "@/components/ShowingResultsText";
import Pagination from "@/components/Pagination";
import {Business} from "@/Api";

export default {
  name: "InventoryItems",
  components: {
    ShowingResultsText,
    Pagination
  },
  props: [
    'businessId',
    'selectingItem'
  ],
  data() {
    return {
      inventoryItems: [],
      currency: null,
      error: null,
      orderCol: null,
      orderDirection: false, // False -> Ascending
      resultsPerPage: 10,
      page: 1,
      loading: false,
      createNewInventoryItem: false
    }
  },
  mounted() {
    this.getCurrencyAndFillTable()
    this.filterAvailableItems()
  },

  computed: {
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
     * Sort InventoryItems Logic
     * @returns {[]|*[]}
     */
    sortedInventoryItems() {
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
    paginatedInventoryItems() {
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
    totalCount() {
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

      let sortVariable = this.orderCol

      //Sorts by product id
      if (this.orderCol === 'productId') {
        a = a['product']
        b = b['product']
        sortVariable = 'id'
      }

      if (a[sortVariable] === null) {
        return -1
      }
      if (b[sortVariable] === null) {
        return 1
      }
      if (a[sortVariable] < b[[sortVariable]]) {
        return 1;
      }
      if (a[sortVariable] > b[[sortVariable]]) {
        return -1;
      }
      return 0;
    },

    /**
     * routes to the edit inventory item page
     * @param id of the inventory item
     */
    editProduct(id) {
      this.$router.push({name: 'editInventoryItem', params: {businessId: this.businessId, inventoryItemId: id}})
    },

    selectProduct(id) {
      this.$parent.inventoryItemId = id;
      this.$parent.updateInventoryItem();
      this.$parent.finishSelectItem();
    },

    /**
     * Uses the getCurrency in the product.js module to get the currency of the business,
     * and then call the fill table method
     */
    async getCurrencyAndFillTable() {
      this.loading = true
      //Change country to businesses address country when implemented
      //The country variable  will always be an actual country as it is a requirement when creating a business
      //Get Businesses country
      const country = (await Business.getBusinessData(parseInt(this.$route.params.businessId))).data.address.country

      this.currency = await this.$root.$data.product.getCurrency(country)

      this.fillTable()
    },

    /**
     * calls the formatPrice method in the product module to format the products recommended retail price
     */
    formatPrice(price) {
      return this.$root.$data.product.formatPrice(this.currency, price)
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

    getMaxQuantity(item) {
      return this.$parent.getMaxQuantity(item);
    },

    /**
     * Fills the table with Product data
     */
    fillTable() {
      this.inventoryItems = [];
      this.loading = true;
      this.page = 1;

      Business.getInventory(this.$route.params.businessId)
          .then((res) => {
            this.error = null;
            this.inventoryItems = res.data;
            this.filterAvailableItems()
            this.loading = false;
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })

    },
    filterAvailableItems() {
      let newInventoryItems = [];

      for (const item of this.inventoryItems) {
        if (this.getMaxQuantity(item) !== 0) {
          newInventoryItems.push(item);
        }
      }

      if (this.selectingItem) {
        this.inventoryItems = newInventoryItems;
      }
    },
    /**
     * Takes user to page to create new inventory item.
     */
    newItem() {
      this.createNewInventoryItem = true;
    },
    refreshInventory() {
      this.createNewInventoryItem = false;
      this.fillTable();
    }
  }
}
</script>

<style scoped>

</style>