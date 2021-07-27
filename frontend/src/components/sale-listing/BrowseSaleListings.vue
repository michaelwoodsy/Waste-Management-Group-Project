<template>
  <page-wrapper>
    <div v-if="isLoggedIn" class="container-fluid">
      <!--    Sale Listings Header    -->
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h4>Browse Sale Listings</h4>
        </div>
      </div>
      <br>

      <div class="row mb-2">
        <div class="col-sm-7">
          <!--    Search Input    -->
          <div class="row form justify-content-center">
            <div class="col-sm-5">
              <div class="input-group">
                <input id="search"
                       v-model="searchQuery"
                       class="form-control no-outline"
                       placeholder="Search listings"
                       type="search"
                       @keyup.enter="checkInputs">
                <div class="input-group-append">
                  <button class="btn btn-primary no-outline" type="button" @click="checkInputs">Search</button>
                </div>
              </div>
            </div>
          </div>

          <!-- Checkboxes for selecting which fields to match -->
          <div class="row form justify-content-center">
            <div class="col form-group text-center">
              <label class="d-inline-block option-title mt-2">Matching Fields</label>
              <br>
              <label v-for="field in fieldOptions"
                     v-bind:key="field.id">
                <input type="checkbox" class="ml-2"
                       v-model="field.checked" v-bind:id="field.id"
                       @click="toggleFieldChecked(field)"
                />
                {{ field.name }}
              </label>
              <br>
              <span v-if="msg.fieldOptions" style="text-align: center; color: red">{{ msg.fieldOptions }}</span>
            </div>
          </div>

          <!-- Order by combobox -->
          <div class="row form justify-content-center">
            <div class="col form-group text-center">
              <label class="d-inline-block option-title mx-2">Order By: </label>
              <select class="form-control d-inline-block w-auto"
                      @change="checkInputs"
                      v-model="orderBy">
                <option
                    :value="orderBy.id"
                    v-bind:key="orderBy.id"
                    v-for="orderBy in orderByOptions"
                >
                  {{ orderBy.name }}
                </option>
              </select>
            </div>
          </div>
        </div>

        <div class="col-sm-5">
          <div class="row form justify-content-center">
            <div class="col form-group text-center">

              <!-- Price range -->
              <label class="d-inline-block option-title mt-2">Price Range:</label>
              <input class="d-inline-block ml-2 w-25"
                     :class="{'form-control': true, 'is-invalid': msg.priceLowerBound}"
                     v-model="priceLowerBound"
              >
              to
              <input class="d-inline-block w-25"
                     :class="{'form-control': true, 'is-invalid': msg.priceUpperBound}"
                     v-model="priceUpperBound"
              >
              <span class="invalid-feedback" style="text-align: center">{{ msg.priceLowerBound }}</span>
              <span class="invalid-feedback" style="text-align: center">{{ msg.priceUpperBound }}</span>
              <br>

              <!-- Closing date range -->
              <label class="d-inline-block option-title mt-2">Closing Date:</label>
              <input id="closingDateLowerBound" v-model="closingDateLowerBound"
                     class="d-inline-block w-25 ml-2"
                     :class="{'form-control': true, 'is-invalid': msg.closingDateLowerBound}"
                     type="date"
              >
              to
              <input id="closingDateUpperBound" v-model="closingDateUpperBound"
                     maxlength="100"
                     class="d-inline-block w-25"
                     :class="{'form-control': true, 'is-invalid': msg.closingDateUpperBound}"
                     type="date"
              >
              <span class="invalid-feedback" style="text-align: center">{{ msg.closingDateLowerBound }}</span>
              <span class="invalid-feedback" style="text-align: center">{{ msg.closingDateUpperBound }}</span>
              <br>
              <button class="btn btn-primary m-2"
                      v-on:click="checkInputs">
                Apply Filters
              </button>
              <button class="btn btn-danger m-2"
                      v-on:click="clearFilters">
                Clear Filters
              </button>
            </div>
          </div>

        </div>
      </div>

      <hr>

      <!-- Sale Listing Information -->
      <div>
        <alert v-if="error">{{ error }}</alert>

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
              <td>{{ formatSeller(listing) }}</td>
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
          />
        </div>
      </div>
    </div>

    <div v-if="viewListingModal" id="viewListingModal" class="modal fade" data-backdrop="static">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-body">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close" @click="viewListingModal=false">
              <span aria-hidden="true">&times;</span>
            </button>
            <individual-sale-listing-modal :listing="listingToView"></individual-sale-listing-modal>
          </div>
        </div>
      </div>
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

export default {
  name: "BrowseSaleListings.vue",
  components: {
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
          name: "Product name",
          checked: false
        },
        {
          id: "sellerName",
          name: "Seller name",
          checked: false
        },
        {
          id: "sellerLocation",
          name: "Seller location",
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

      totalCount: 0
    }
  },
  mounted() {
    this.search()
  },
  computed: {
    /**
     * Checks if user is logged in
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },
  },
  methods: {
    /**
     * Function is called by pagination component to make another call to the backend
     * to update the list of users that should be displayed
     */
    async changePage() {
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
     * Formats the name and address of the business offering the listing
     */
    formatSeller(listing) {
      return `${listing.business.name} from ${this.$root.$data.address.formatAddress(listing.business.address)}`
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
        this.search()
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
      this.search()
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
        } else if (parseFloat(this.priceUpperBound) < parseFloat(this.priceLowerBound)){
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
        const filteredImages = product.images.filter(function(specificImage) {
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
     * Turns popup modal to view  business on
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
            // this.listings = await Promise.all(this.listings.map(async (listing) => {
            //   const businessResponse = await Business.getBusinessData(listing.businessId)
            //   listing.sellerName = businessResponse.data.name
            //   listing.sellerAddress = businessResponse.data.address
            //   return listing
            // }))
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
  }
}
</script>

<style scoped>

.option-title {
  font-size: 18px;
}

</style>