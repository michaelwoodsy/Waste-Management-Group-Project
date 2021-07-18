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


      <!-- TODO: table of sale listings goes here.
      Includes all fields from SaleListings, plus business info -->
    </div>
  </page-wrapper>

</template>

<script>
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "BrowseSaleListings.vue",
  components: {
    PageWrapper
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
    },
  },
  methods: {
    /**
     * Toggles whether a field is selected to be searched by
     */
    toggleFieldChecked(field) {
      field.checked = !(field.checked);
    },

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
            this.msg.closingDateUpperBound = "The closing date's upper bound is before the lower bound"
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