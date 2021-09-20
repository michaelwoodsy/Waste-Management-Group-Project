<template>
  <page-wrapper>

    <login-required
        v-if="!isLoggedIn"
        page="browse sale listings"
    />

    <div v-else class="container-fluid">
      <!--    Sale Listings Header    -->
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h4>Browse Sale Listings</h4>
        </div>
      </div>
      <br>

      <!-- Search Option -->
      <div class="mb-4">
        <div class="row justify-content-center">
          <div class="col-xl-6 col-lg-8 col-md-10 text-center">
            <!--    Search Input    -->
            <div class="form-group row">
              <div class="input-group">
                <input id="search"
                       v-model="searchQuery"
                       class="form-control no-outline"
                       placeholder="Search listings"
                       type="search"
                       @keyup.enter="checkInputs">
                <div class="input-group-append">
                  <button :class="{'btn-outline-secondary': !optionsShow, 'btn-secondary': optionsShow}" class="btn"
                          data-target="#searchOptions" data-toggle="collapse" type="button"
                          @click="optionsShow = !optionsShow"
                  >
                    <span v-if="optionsShow">Close</span>
                    <span v-else>Options</span>
                  </button>
                  <button class="btn btn-primary no-outline" type="button" @click="checkInputs">Search</button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div id="searchOptions" class="row justify-content-center collapse">
          <div class="col-xl-6 col-lg-8 col-md-10 text-center">
            <!-- Checkboxes for selecting which fields to match -->
            <div class="form-group row">
              <div :class="{'is-invalid': msg.fieldOptions}" class="input-group">
                <div class="input-group-prepend">
                  <span class="input-group-text">Search By</span>
                </div>
                <div :class="{'is-invalid': msg.fieldOptions}" class="form-control d-flex justify-content-around">
                  <div v-for="field in fieldOptions" :key="field.id" class="custom-control custom-checkbox">
                    <input :id="field.id" v-model="field.checked"
                           class="custom-control-input" type="checkbox"
                           @click="toggleFieldChecked(field)"
                    />
                    <label :for="field.id" class="custom-control-label">{{ field.name }}</label>
                  </div>
                </div>
              </div>
              <span class="invalid-feedback">{{ msg.fieldOptions }}</span>
            </div>
            <!-- Order by combobox -->
            <div class="form-group row">
              <div class="input-group">
                <div class="input-group-prepend">
                  <span class="input-group-text">Order By</span>
                </div>
                <select v-model="orderBy" class="form-control custom-select" @change="checkInputs">
                  <option v-for="orderBy in orderByOptions" :key="orderBy.id" :value="orderBy.id">
                    {{ orderBy.name }}
                  </option>
                </select>
              </div>
            </div>
            <!-- Price Range -->
            <div class="form-group row">
              <div :class="{'is-invalid': msg.priceLowerBound || msg.priceUpperBound}" class="input-group">
                <div class="input-group-prepend">
                  <span class="input-group-text">Price Range</span>
                </div>
                <input v-model="priceLowerBound"
                       :class="{'is-invalid': msg.priceLowerBound}"
                       class="form-control" placeholder="minimum price"
                >
                <input v-model="priceUpperBound"
                       :class="{'is-invalid': msg.priceUpperBound}"
                       class="form-control" placeholder="maximum price"
                >
              </div>
              <span class="invalid-feedback" style="text-align: center">{{ msg.priceLowerBound }}</span>
              <span class="invalid-feedback" style="text-align: center">{{ msg.priceUpperBound }}</span>
            </div>
            <!-- Date Range -->
            <div class="form-group row">
              <div :class="{'is-invalid': msg.closingDateLowerBound || msg.closingDateUpperBound}" class="input-group">
                <div class="input-group-prepend">
                  <span class="input-group-text">Closing Date Range</span>
                </div>
                <input id="closingDateLowerBound" v-model="closingDateLowerBound"
                       :class="{'form-control': true, 'is-invalid': msg.closingDateLowerBound}"
                       class="form-control" type="date"
                >
                <input id="closingDateUpperBound" v-model="closingDateUpperBound"
                       :class="{'form-control': true, 'is-invalid': msg.closingDateUpperBound}"
                       class="form-control" type="date"
                >
              </div>
              <span class="invalid-feedback" style="text-align: center">{{ msg.closingDateLowerBound }}</span>
              <span class="invalid-feedback" style="text-align: center">{{ msg.closingDateUpperBound }}</span>
            </div>
            <!-- Filter Buttons -->
            <div class="btn-group btn-block w-50">
              <button class="btn btn-primary w-50"
                      @click="filter">
                Apply Filters
              </button>
              <button v-if="filtered"
                      class="btn btn-danger w-50"
                      @click="clearFilters">
                Clear Filters
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Sale Listing Information -->
      <div>
        <alert v-if="error">{{ error }}</alert>
        <!-- Table of results -->
        <div class="overflow-auto">
          <table aria-label="Table of Sale Listings"
                 class="table table-hover"
          >
            <thead>
            <tr>
              <!-- Product Image -->
              <th scope="col"></th>
              <!-- Product Info -->
              <th scope="col">
                <p class="d-inline">Product Info</p>
              </th>
              <!-- Quantity of product being sold -->
              <th scope="col">
                <p class="d-inline">Quantity</p>
              </th>
              <!-- Price of listing -->
              <th scope="col">
                <p class="d-inline">Price</p>
              </th>
              <!-- Date listing was created -->
              <th scope="col">
                <p class="d-inline">Created</p>
              </th>
              <!-- Date listing closes -->
              <th scope="col">
                <p class="d-inline">Closes</p>
              </th>
              <!-- Seller's details -->
              <th scope="col">
                <p class="d-inline">Seller</p>
              </th>
            </tr>
            </thead>
            <tbody v-if="!loading">
            <tr v-for="listing in listings"
                v-bind:key="listing.id"
                class="pointer"
                data-target="#viewListingModal"
                data-toggle="modal"
                @click="viewListing(listing)"
            >
              <td>
                <img :src="getPrimaryImageThumbnail(listing.inventoryItem.product)" alt="productImage"
                     class="ui-icon-image">
              </td>
              <td style="word-break: break-word; width: 35%">
                {{ listing.inventoryItem.product.name }}<br>
                <em class="bi" style="color: red"
                    :class="{'bi-heart-fill': listing.userLikes, 'bi-heart': !listing.userLikes}"
                /> {{ listing.likes }}
                <span v-if="listing.moreInfo" style="font-size: small"><br/>{{ listing.moreInfo }}</span>
              </td>
              <td>{{ listing.quantity }}</td>
              <td>{{ formatPrice(listing) }}</td>
              <td>{{ formatDate(listing.created) }}</td>
              <td>{{ formatDate(listing.closes) }}</td>
              <td style="word-break: break-word; width: 20%">
                {{ listing.business.name }}<br>
                <span class="text-muted small">{{ listing.business.businessType }}</span><br>
                <span class="text-muted small">{{ formatAddress(listing.business.address) }}</span>
              </td>
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
          <div class="mb-2 text-center">
            <showing-results-text
                :items-per-page="resultsPerPage"
                :page="page"
                :total-count="totalCount"
            />
          </div>
          <div>
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

    <!-- Sale listing modal -->
    <div v-if="viewListingModal">
      <individual-sale-listing-modal :listing="listingToView"
                                     @update-listings="checkInputs"
                                     @close-modal="viewListingModal = false"
                                     v-bind:key="listingToView.id"
                                     id="saleListingModal"
      />
    </div>

  </page-wrapper>

</template>

<script>
import PageWrapper from "@/components/PageWrapper";
import Pagination from "@/components/Pagination";
import ShowingResultsText from "@/components/ShowingResultsText";
import {Business, Images} from "@/Api";
import IndividualSaleListingModal from "@/components/sale-listing/IndividualSaleListingModal";
import Alert from "@/components/Alert";
import LoginRequired from "@/components/LoginRequired";
import $ from 'jquery'

export default {
  name: "BrowseSaleListings.vue",
  components: {
    LoginRequired,
    IndividualSaleListingModal,
    PageWrapper,
    Pagination,
    ShowingResultsText,
    Alert
  },
  data() {
    return {
      searchQuery: "",
      fieldOptions: [
        {
          id: "productName",
          name: "Product Name",
          checked: true
        },
        {
          id: "sellerName",
          name: "Seller Name",
          checked: false
        },
        {
          id: "sellerLocation",
          name: "Seller Location",
          checked: false
        },
        {
          id: "sellerType",
          name: "Seller Type",
          checked: false
        }
      ],
      priceLowerBound: null,
      priceUpperBound: null,
      closingDateLowerBound: null,
      closingDateUpperBound: null,
      valid: true,
      msg: {
        priceLowerBound: '',
        priceUpperBound: '',
        closingDateLowerBound: '',
        closingDateUpperBound: '',
        fieldOptions: ''
      },
      orderBy: "bestMatch",
      orderByOptions: [
        {id: "bestMatch", name: "Best Match"},
        {id: "priceAsc", name: "Price Low"},
        {id: "priceDesc", name: "Price High"},
        {id: "productName", name: "Product Name"},
        {id: "country", name: "Country"},
        {id: "city", name: "City"},
        {id: "expiryDateAsc", name: "Expiry Date Soonest"},
        {id: "expiryDateDesc", name: "Expiry Date Latest"},
        {id: "seller", name: "Seller"}
      ],
      loading: false,
      resultsPerPage: 10,
      page: 1,
      listings: [],
      listingToView: null,
      viewListingModal: false,
      error: null,
      totalCount: 0,
      filtered: false,
      optionsShow: false
    }
  },
  mounted() {
    this.search()
    this.checkFeaturedListing()
  },
  watch: {
    $route(val) {
      // required if the user clicks on a featured listing from within this page
      if (val.name === "browseListings") {
        this.checkFeaturedListing()
      }
    }
  },
  computed: {
    /**
     * Checks if user is logged in
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    }
  },
  methods: {
    /**
     * Check if a featured listing should be shown based on businessId and listingId query parameters.
     */
    async checkFeaturedListing() {
      // Check if a listing is specified in query params
      if (this.$route.query.businessId && this.$route.query.listingId) {

        // get the featured listing
        let res = await Business.getFeaturedListings(this.$route.query.businessId)
        let featuredListing = res.data.find(listing => `${listing.id}` === `${this.$route.query.listingId}`)

        // Add currency to the listing
        let listings = await this.$root.$data.product.addSaleListingCurrencies(
            [featuredListing], featuredListing.business.address.country)

        // Set listing as being viewed
        this.viewListing(listings[0])

        window.setTimeout(() => {$('#saleListingModal').modal('show')}, 500)
      }
    },

    /**
     * Function is called by pagination component to make another call to the backend
     * to update the list of users that should be displayed
     */
    async changePage(page) {
      this.page = page
      this.loading = true;
      await this.search()
    },

    /**
     * Formats the price of a listing based on
     * the country of the business offering the listing
     */
    formatPrice(listing) {
      return this.$root.$data.product.formatPrice(listing.currency, listing.price);
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
     * Formats the address of the business offering the listing
     */
    formatAddress(address) {
      return `${this.$root.$data.address.formatAddress(address)}`
    },

    /**
     * Toggles whether a field is selected to be searched by
     */
    toggleFieldChecked(field) {
      field.checked = !(field.checked);
    },

    /**
     * Checks the user's input for the filter options and
     * calls the search function if the input is all valid
     */
    checkInputs() {
      this.valid = true
      this.validatePrices()
      this.validateDates()
      this.validateFieldOptions()

      if (this.valid) {
        this.page = 1
        this.search()
      }
    },

    /**
     * Sets filtered to true and then searches
     */
    filter() {
      if (this.priceLowerBound !== null ||
          this.priceUpperBound !== null ||
          this.closingDateLowerBound !== null ||
          this.closingDateUpperBound !== null) {
        this.filtered = true
        this.checkInputs()
      }
    },

    /**
     * Clears the price and date filters and searches again
     */
    clearFilters() {
      this.priceLowerBound = null
      this.priceUpperBound = null
      this.closingDateLowerBound = null
      this.closingDateUpperBound = null
      this.filtered = false
      this.checkInputs()
    },

    /**
     *  Checks the values of priceLowerBound and priceUpperBound
     */
    validatePrices() {
      this.msg.priceLowerBound = null;
      if (this.priceLowerBound != null) {
        let lowerPriceNotNumber = Number.isNaN(Number(this.priceLowerBound))
        if (lowerPriceNotNumber || !/^([0-9]+(.[0-9]{0,2})?)?$/.test(this.priceLowerBound)) {
          this.msg.priceLowerBound = "Please enter a valid price for the price's lower bound";
          this.valid = false
        }
      }

      this.msg.priceUpperBound = null
      if (this.priceUpperBound != null) {
        let upperPriceNotNumber = Number.isNaN(Number(this.priceUpperBound))
        if (upperPriceNotNumber || !/^([0-9]+(.[0-9]{0,2})?)?$/.test(this.priceUpperBound)) {
          this.msg.priceUpperBound = "Please enter a valid price for the price's upper bound";
          this.valid = false
        } else if (parseFloat(this.priceUpperBound) < parseFloat(this.priceLowerBound)) {
          this.msg.priceUpperBound = "The price's upper bound is less than the lower bound"
          this.valid = false
        }
      }
    },

    /**
     *  Checks the values of closingDateLowerBound and closingDateUpperBound
     */
    validateDates() {

      this.msg.closingDateLowerBound = null
      if (this.closingDateLowerBound != null) {
        let dateNow = new Date()
        let lowerDateGiven = new Date(this.closingDateLowerBound)

        if ((lowerDateGiven - dateNow <= 0)) {
          this.msg.closingDateLowerBound = "Please enter a date in the future for the closing date's lower bound"
          this.valid = false
        }
      }

      this.msg.closingDateUpperBound = null
      if (this.closingDateUpperBound != null) {
        let dateNow = new Date()
        let upperDateGiven = new Date(this.closingDateUpperBound)

        if ((upperDateGiven - dateNow <= 0)) {
          this.msg.closingDateUpperBound = "Please enter a date in the future for the closing date's upper bound"
          this.valid = false
        } else if (this.closingDateLowerBound != null) {
          let lowerDate = new Date(this.closingDateLowerBound)
          if ((upperDateGiven - lowerDate < 0)) {
            this.msg.closingDateUpperBound = "The closing date's upper bound is earlier than the lower bound"
            this.valid = false
          }
        }
      }
    },

    validateFieldOptions() {
      this.msg.fieldOptions = null
      //If no field options are checked
      if (!this.fieldOptions[0].checked &&
          !this.fieldOptions[1].checked &&
          !this.fieldOptions[2].checked &&
          !this.fieldOptions[3].checked &&
          this.searchQuery !== "") {
        this.msg.fieldOptions = "Please select a field to search by"
        this.valid = false
      }
    },

    /**
     * Uses the primaryImageId of the product to find the primary image and return its imageURL,
     * else it returns the default product image url
     */
    getPrimaryImageThumbnail(product) {
      if (product.primaryImageId !== null) {
        const filteredImages = product.images.filter(function (specificImage) {
          return specificImage.id === product.primaryImageId;
        })
        if (filteredImages.length === 1) {
          return this.getImageURL(filteredImages[0].thumbnailFilename)
        }
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
     * Turns popup modal to view  listing on
     * @param listing the listing object for the modal to show
     */
    viewListing(listing) {
      this.listingToView = listing
      this.viewListingModal = true
    },

    /**
     * Applies the user's search input
     */
    async search() {
      this.loading = true
      await Business.searchSaleListings(
          this.searchQuery,
          this.fieldOptions[0].checked,
          this.fieldOptions[1].checked,
          this.fieldOptions[2].checked,
          this.fieldOptions[3].checked,
          this.priceLowerBound,
          this.priceUpperBound,
          this.closingDateLowerBound,
          this.closingDateUpperBound,
          this.page - 1,
          this.orderBy)
          .then(async (res) => {
            this.listings = res.data[0]
            this.listings = await this.$root.$data.product.addSaleListingCurrencies(this.listings)
            this.totalCount = res.data[1]
            this.error = null
            this.loading = false
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })
    },
  },
  beforeRouteLeave: (nextRoute, curRoute, next) => {
    // close all modals when leaving this page
    // this.viewListingModal = false
    $('.modal').modal('hide');
    next()
  }
}
</script>

<style scoped>

</style>