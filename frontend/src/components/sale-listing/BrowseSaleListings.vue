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
                       v-model="searchTerm"
                       class="form-control no-outline"
                       placeholder="Search listings"
                       type="search"
                       @keyup.enter="search">
                <div class="input-group-append">
                  <button class="btn btn-primary no-outline" type="button" @click="search">Search</button>
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
            </div>
          </div>

          <!-- Order by combobox -->
          <div class="row form justify-content-center">
            <div class="col form-group text-center">
              <label class="d-inline-block option-title mx-2">Order By: </label>
              <select class="form-control d-inline-block w-auto"
                      @change="search"
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
            </div>
          </div>

        </div>
      </div>

      <hr>


      <!-- TODO: add business info to the table -->
      <!-- Sale Listing Information -->
      <div>

        <!-- TODO: Fix table to have correct variables -->
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
              <!--    view images button column    -->
              <th scope="col"></th>
            </tr>
            </thead>
            <tbody v-if="!loading">
            <tr v-for="item in paginatedListings" v-bind:key="item.id">
              <td>
                <img alt="productImage" class="ui-icon-image"
                     :src="getPrimaryImageThumbnail(item.inventoryItem.product)">
              </td>
              <td style="word-break: break-word; width: 50%">
                {{ item.inventoryItem.product.name }}
                <span v-if="item.moreInfo" style="font-size: small"><br/>{{ item.moreInfo }}</span>
              </td>
              <td>{{ item.quantity }}</td>
              <td>{{ formatPrice(item.price) }}</td>
              <td>{{ formatDate(item.created) }}</td>
              <td>{{ formatDate(item.closes) }}</td>
              <td>
                <button class="btn btn-primary" data-target="#viewImages" data-toggle="modal"
                        @click="changeViewedProduct(item.inventoryItem.product)">View Images</button>
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
          <pagination
              :current-page.sync="page"
              :items-per-page="resultsPerPage"
              :total-items="totalCount"
          />
        </div>
      </div>
    </div>

    <!--   Product images modal   -->
    <div v-if="isViewingImages" id="viewImages" class="modal fade" data-backdrop="static">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{productViewing.name}}'s Images</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close" @click="isViewingImages=false">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <div v-if="productViewing.images.length === 0">
              <p class="text-center"><strong>This Product has no Images</strong></p>
            </div>
            <div v-else class="row" style="height: 500px">
              <div class="col col-12 justify-content-center">
                <div id="imageCarousel" class="carousel slide" data-ride="carousel">
                  <div class="carousel-inner">
                    <div v-for="(image, index) in productViewing.images" v-bind:key="image.id"
                         :class="{'carousel-item': true, 'active': index === 0}">
                      <img class="d-block img-fluid rounded mx-auto d-block" style="height: 500px" :src="getImageURL(image.filename)" alt="ProductImage">
                    </div>
                  </div>
                  <a class="carousel-control-prev" href="#imageCarousel" role="button" data-slide="prev">
                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span class="sr-only">Previous</span>
                  </a>
                  <a class="carousel-control-next" href="#imageCarousel" role="button" data-slide="next">
                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    <span class="sr-only">Next</span>
                  </a>
                </div>
              </div>
            </div>
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

export default {
  name: "BrowseSaleListings.vue",
  components: {
    PageWrapper,
    Pagination,
    ShowingResultsText
  },
  data() {
    return {
      searchTerm: "",
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
        closingDateUpperBound: ''
      },
      orderBy: "bestMatch",
      orderByOptions: [
          {id: "bestMatch", name: "Best Match"},
          {id: "priceAsc", name: "Price Low"},
          {id: "priceDesc", name: "Price High"},
          {id: "productName", name: "Product Name"},
          {id: "country", name: "Country"},
          {id: "city", name: "City"},
          {id: "expiryDate", name: "Expiry Date"},
          //TODO: add expiry date desc and update API spec
          {id: "seller", name: "Seller"}
      ]
    }
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

      if (this.valid) {
        this.search()
      }
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
        } else if (this.priceUpperBound < this.priceLowerBound){
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

    /**
     * Applies the user's search input
     */
    search() {
      //TODO: implement me!
    },
  }
}
</script>

<style scoped>

.option-title {
  font-size: 18px;
}

</style>