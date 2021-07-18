<template>
  <page-wrapper>
    <!--    Sale Listings Header    -->
    <div class="row">
      <div class="col-12 text-center mb-2">
        <h4>Browse Sale Listings</h4>
      </div>
    </div>

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

    <!--Price and date ranges -->
    <div class="row form justify-content-center">
      <div class="col form-group text-center">
        <label class="d-inline-block option-title mt-2">Price Range:</label>
        <input class="ml-2 w-25" v-model="priceLowerBound">
        to
        <input class="w-25" v-model="priceUpperBound">
        <br>
        <label class="d-inline-block option-title mt-2">Closing Date:</label>
        <input id="closingDateLowerBound" v-model="closingDateLowerBound"
               class="form-control d-inline-block w-25 ml-2"
               type="date"
        >
        to
        <input id="closingDateUpperBound" v-model="closingDateUpperBound"
               maxlength="100"
               class="form-control d-inline-block w-25"
               type="date"
        >
      </div>
    </div>

    <!-- TODO: table of sale listings goes here.
    Includes all fields from SaleListings, plus business info -->
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
      closingDateUpperBound: null
    }
  },
  methods: {
    /**
     * Toggles whether a field is selected to be searched by
     */
    toggleFieldChecked(field) {
      field.checked = !(field.checked);
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