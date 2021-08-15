<template>
  <page-wrapper>

    <login-required
        v-if="!isLoggedIn"
        page="view this business's sale listings"
    />

    <div v-else class="container-fluid">
      <div class="row justify-content-center">
        <div class="col">

          <!-- Sale Listings Header -->
          <div class="row">
            <div class="col"/>
            <div class="col text-center">
              <h4>Sale Listings</h4>
            </div>
            <div class="col text-right">
              <button v-if="isAdminOf" class="btn btn-primary" data-target="#createListing" data-toggle="modal"
                      @click="newListing">
                New Listing
              </button>
              <!-- GAA/DGAA button to add sales listings -->
              <button v-else-if="$root.$data.user.canDoAdminAction()" class="btn btn-danger"
                      data-target="#createListing" data-toggle="modal"
                      @click="newListing">
                New Listing
              </button>
            </div>
          </div>

          <!-- Error Alert -->
          <div v-if="error" class="row">
            <div class="col text-center">
              <alert>{{ error }}</alert>
            </div>
          </div>

          <!-- Sale Listing Information -->
          <div>

            <!-- Number of results information -->
            <div class="text-center">
              <showing-results-text
                  :items-per-page="resultsPerPage"
                  :page="page"
                  :total-count="totalCount"
              />
            </div>

            <!-- Table of results -->
            <div class="overflow-auto">
              <table class="table table-hover"
                     aria-label="Table of Sale Listings"
              >
                <thead>
                <tr>
                  <!-- Product Image -->
                  <th scope="col"></th>
                  <!-- Product Info -->
                  <th class="pointer" scope="col" @click="orderResults('name')">
                    <p class="d-inline">Product Info</p>
                    <p v-if="orderCol === 'name'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                  <!-- Quantity of product being sold -->
                  <th class="pointer" scope="col" @click="orderResults('quantity')">
                    <p class="d-inline">Quantity</p>
                    <p v-if="orderCol === 'quantity'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                  <!-- Price of listing -->
                  <th class="pointer" scope="col" @click="orderResults('price')">
                    <p class="d-inline">Price</p>
                    <p v-if="orderCol === 'price'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                  <!-- Date listing was created -->
                  <th class="pointer" scope="col" @click="orderResults('created')">
                    <p class="d-inline">Created</p>
                    <p v-if="orderCol === 'created'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                  <!-- Date listing closes -->
                  <th class="pointer" scope="col" @click="orderResults('closes')">
                    <p class="d-inline">Closes</p>
                    <p v-if="orderCol === 'closes'" class="d-inline">{{ orderDirArrow }}</p>
                  </th>
                </tr>
                </thead>
                <tbody v-if="!loading">
                <tr v-for="listing in paginatedListings"
                    v-bind:key="listing.id"
                    class="pointer"
                    data-target="#viewListingModal"
                    data-toggle="modal"
                    @click="viewListing(listing)"
                >
                  <td>
                    <img alt="productImage" class="ui-icon-image"
                         :src="getPrimaryImageThumbnail(listing.inventoryItem.product)">
                  </td>
                  <td style="word-break: break-word; width: 50%">
                    {{ listing.inventoryItem.product.name }}
                    <span v-if="listing.moreInfo" style="font-size: small"><br/>{{ listing.moreInfo }}</span>
                  </td>
                  <td>{{ listing.quantity }}</td>
                  <td>{{ formatPrice(listing) }}</td>
                  <td>{{ formatDate(listing.created) }}</td>
                  <td>{{ formatDate(listing.closes) }}</td>
                </tr>
                </tbody>
              </table>
            </div>

          </div>

          <!-- Show loading text until results are obtained -->
          <div v-if="loading" class="row">
            <span class="col text-center text-muted">Loading...</span>
          </div>

          <!--    Result Information    -->
          <div class="row">
            <div class="col">
              <pagination
                  :current-page.sync="page"
                  :items-per-page="resultsPerPage"
                  :total-items="totalCount"
                  @change-page="changePage"
              />
            </div>
          </div>

        </div>
      </div>
    </div>

    <div id="createListing" :key="this.createNewListing" class="modal fade" data-backdrop="static">
      <div ref="createListingWindow" class="modal-dialog modal-open" :class="{'modal-xl': selectingInventoryItem}">
        <div class="modal-content">
          <div class="modal-body">
            <create-listing @refresh-listings="refreshListings"
                            @select-inventory-item-toggle="selectingInventoryItem = !selectingInventoryItem"></create-listing>
          </div>
        </div>
      </div>
    </div>

    <div v-if="viewListingModal" id="viewListingModal" class="modal fade" data-backdrop="static">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-body">
            <button aria-label="Close" class="close" data-dismiss="modal" type="button" @click="viewListingModal=false">
              <span aria-hidden="true">&times;</span>
            </button>
            <individual-sale-listing-modal :listing="listingToView" @viewBusiness="viewBusiness" @updateListings="fillTable()"></individual-sale-listing-modal>
          </div>
        </div>
      </div>
    </div>

    <div v-if="viewBusinessModal" id="viewBusinessModal" class="modal fade" data-backdrop="static">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-body">
            <button aria-label="Close" class="close" data-dismiss="modal" type="button" @click="viewBusinessModal=false">
              <span aria-hidden="true">&times;</span>
            </button>
            <business-profile-page-modal business="businessToViewId"></business-profile-page-modal>
          </div>
        </div>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import Alert from "@/components/Alert";
import ShowingResultsText from "@/components/ShowingResultsText";
import Pagination from "@/components/Pagination";
import {Business, Images} from "@/Api";
import IndividualSaleListingModal from "@/components/sale-listing/IndividualSaleListingModal";
import BusinessProfilePageModal from "@/components/business/BusinessProfilePageModal";
import CreateListing from "@/components/sale-listing/CreateListing";
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "SaleListings",
  components: {
    PageWrapper,
    IndividualSaleListingModal,
    BusinessProfilePageModal,
    CreateListing,
    ShowingResultsText,
    LoginRequired,
    Alert,
    Pagination
  },

  data() {
    return {
      listings: [],
      currency: {
        symbol: "",
        code: ""
      },
      error: null,
      orderCol: null,
      orderDirection: false, // False = Ascending
      resultsPerPage: 10,
      page: 1,
      loading: false,
      createNewListing: false,
      selectingInventoryItem: false,

      listingToView: null,
      viewListingModal: false,
      viewBusinessModal: false,
      businessToViewId: null,
    }
  },

  mounted() {
    this.getCurrency();
    this.fillTable();
  },

  computed: {
    /**
     * Gets the business ID
     * @returns {any} the business ID for which this listing page corresponds to
     */
    businessId() {
      return parseInt(this.$route.params.businessId);
    },

    /**
     * Returns whether or not the user is logged in.
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn;
    },

    /**
     * Check if the user is an admin of the business and is acting as that business
     */
    isAdminOf() {
      if (this.$root.$data.user.state.actingAs.type !== "business") return false
      return this.$root.$data.user.state.actingAs.id === this.businessId;
    },

    /**
     * Checks which direction (ascending or descending) the order by should be
     * @returns {string}
     */
    orderDirArrow() {
      if (this.orderDirection) {
        return '↓';
      } else {
        return '↑';
      }
    },

    /**
     * Returns sales listings after sorting.
     */
    sortedListings() {
      if (this.orderCol === null) {
        return this.listings;
      }
      let newListings = [...this.listings];
      const orderDir = this.orderDirection ? 1 : -1;
      if (newListings.length > 0) {
        newListings.sort((a, b) => {
          return orderDir * this.sortAlpha(a, b);
        });
      }
      return newListings;
    },

    /**
     * Returns paginated sale listings.
     */
    paginatedListings() {
      let newListings = this.sortedListings;
      if (newListings.length > 0) {
        const startIndex = this.resultsPerPage * (this.page - 1);
        const endIndex = this.resultsPerPage * this.page;
        newListings = newListings.slice(startIndex, endIndex);
      }
      return newListings;
    },

    /**
     * The total number of listings.
     */
    totalCount() {
      return this.listings.length;
    },
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
     * Updates the current page
     */
    changePage(page) {
      this.page = page
    },

    /**
     * Turns popup modal to view  listing on
     * @param listing the listing object for the modal to show
     */
    viewListing(listing) {
      this.viewBusinessModal = false
      this.listingToView = listing
      this.viewListingModal = true
    },

    /**
     * Turns popup modal to view  business on
     * @param listing the listing object with the business information to show
     */
    viewBusiness(listing) {
      this.viewListingModal = false
      this.businessToViewId = listing.business.id
      this.viewBusinessModal = true
    },

    /**
     * Uses the primaryImageId of the product to find the primary image and return its imageURL,
     * else it returns the default product image url
     */
    getPrimaryImageThumbnail(product) {
      if (product.primaryImageId === null) {
        return this.getImageURL('/media/defaults/defaultProduct_thumbnail.jpg')
      }
      const filteredImages = product.images.filter(function(specificImage) {
        return specificImage.id === product.primaryImageId;
      })
      if (filteredImages.length === 1) {
        return this.getImageURL(filteredImages[0].thumbnailFilename)
      }
      //Return the default image if the program gets to this point (if it does something went wrong)
      return this.getImageURL('/media/defaults/defaultProduct_thumbnail.jpg')
    },
    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
    },
    /**
     * Method which sets the column and direction to order by.
     */
    orderResults(col) {
      if (this.orderCol === col && this.orderDirection) {
        this.orderCol = null;
        this.orderDirection = false;
        return;
      }

      this.orderDirection = this.orderCol === col;
      this.orderCol = col;
    },

    /**
     * Function which returns the value of a specified column. Used for sorting.
     * @param item the item to get field from.
     * @returns value of the specified column.
     */
    getCol(item) {
      switch (this.orderCol) {
        case 'name':
          return item.inventoryItem.product.name;
        case 'quantity':
          return item.quantity;
        case 'price':
          return item.price;
        case 'created':
          return item.created;
        case 'closes':
          return item.closes;
        default:
          return item.id;
      }
    },

    /**
     * Function that sorts alphabetically by orderCol.
     */
    sortAlpha(a, b) {
      if (this.getCol(a) === null) {
        return -1
      }
      if (this.getCol(b) === null) {
        return 1
      }
      if (this.getCol(a) < this.getCol(b)) {
        return 1;
      }
      if (this.getCol(a) > this.getCol(b)) {
        return -1;
      }
      return 0;
    },

    /**
     * Formats the date fields.
     * @param string string representation of the date.
     * @returns {string} the formatted date.
     */
    formatDate(string) {
      if (string === '') {
        return '';
      } else {
        return new Date(string).toDateString();
      }
    },

    /**
     * Formats the price to correct currency.
     */
    formatPrice(listing) {
      return this.$root.$data.product.formatPrice(listing.currency, listing.price);
    },

    /**
     * Gets the currency to use for a particular business.
     */
    async getCurrency() {
      const country = (await Business.getBusinessData(this.businessId)).data.address.country;
      this.currency = await this.$root.$data.product.getCurrency(country);
    },

    /**
     * Fills the table with a business's sale listings.
     */
    fillTable() {
      this.listings = [];
      this.loading = true;
      this.page = 1;

      Business.getListings(this.$route.params.businessId)
          .then(async (res) => {
            this.error = null;
            const business = (await Business.getBusinessData(this.businessId)).data
            this.listings = await this.$root.$data.product.addSaleListingCurrencies(res.data, business.address.country)
            this.loading = false;
          })
          .catch((err) => {
            this.error = err;
            console.error(err)
            this.loading = false;
          })
    },

    /**
     * Takes user to page to add a new listing.
     */
    newListing() {
      this.createNewListing = true;
    },
    refreshListings() {
      this.createNewListing = false;
      this.fillTable();
    }
  }
}
</script>

<style scoped>

</style>