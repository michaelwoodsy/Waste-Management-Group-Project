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
        <label class="d-inline-block fields-title mt-2">Matching Fields</label>
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
      ]
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
    }
  }
}
</script>

<style scoped>

</style>