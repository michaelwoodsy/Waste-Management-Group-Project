<!-- Product Search component used in the Product Catalogue
  for business admin to search their products -->

<template>
  <page-wrapper>
    <!--    Search Input    -->
    <div class="row form justify-content-center">
      <div class="col-sm-5">
        <div class="input-group">
          <input id="search"
                 v-model="searchTerm"
                 class="form-control no-outline"
                 placeholder="Search products"
                 type="text"
                 @keyup.enter="search"
                 data-toggle="dropdown"
                 @input="searchProductNames">
          <div class="input-group-append">
            <button class="btn btn-primary no-outline" type="button" @click="search">Search</button>
          </div>
          <!-- Autocomplete dropdown -->
          <div class="dropdown-menu overflow-auto" id="dropdown">
            <!-- If no user input -->
            <p class="text-muted dropdown-item left-padding mb-0 disabled"
               v-if="searchTerm.length === 0"
            >
              Start typing...
            </p>
            <!-- If no matches -->
            <p class="text-muted dropdown-item left-padding mb-0 disabled"
               v-else-if="filteredProducts.length === 0 && searchTerm.length > 0"
            >
              No results found.
            </p>
            <!-- If there are matches -->
            <a class="dropdown-item pointer left-padding"
               v-for="product in filteredProducts"
               v-else
               :key="product.id"
               @click="searchTerm=product.id">
              <span>{{ product.id }}</span>
            </a>
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
               v-bind:key="field.name">
          <input type="checkbox" class="ml-2"
                 v-model="field.checked" v-bind:id="field.name"
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
import {Business} from "@/Api";

export default {
  name: "ProductSearch",
  components: {
    PageWrapper
  },
  data() {
    return {

      searchTerm: "",
      filteredProducts: [],
      fieldOptions: [
        {
          name: "Id",
          checked: false
        },
        {
          name: "Name",
          checked: true //Default is to search only by name
        },
        {
          name: "Description",
          checked: false
        },
        {
          name: "Manufacturer",
          checked: false
        }
      ]
    }
  },
  computed: {
    /**
     * Gets the business ID
     * @returns {any}
     */
    businessId() {
      return this.$route.params.businessId;
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
     * Applies the user's search input
     */
    search() {
      Business.searchProducts(this.businessId, this.searchTerm,
          this.fieldOptions[0].checked,
          this.fieldOptions[1].checked,
          this.fieldOptions[2].checked,
          this.fieldOptions[3].checked,
          )
          .then((response) => {
            if(this.$parent.$options._componentTag === "create-inventory-item"){
              //This is for in the product look up in inventory
              this.$parent.applySearch(response.data)
            } else {
              //This is for the product catalogue
              this.$parent.$parent.applySearch(response.data)
            }
      })
          .catch((err) => {
            console.log(`There was an error searching products: ${err}`)
      })
    },

    /**
     * Filters autocomplete options based on the user's input for a product.
     */
     searchProductNames() {
      Business.searchProducts(this.businessId, this.searchTerm,
          this.fieldOptions[0].checked,
          this.fieldOptions[1].checked,
          this.fieldOptions[2].checked,
          this.fieldOptions[3].checked,
      )
          .then((response) => {
            this.filteredProducts = response.data
          })
          .catch((err) => {
            console.log(`There was an error searching products: ${err}`)
          })
    }
  }
}
</script>

<style scoped>

.fields-title {
  font-size: 18px;
}

</style>