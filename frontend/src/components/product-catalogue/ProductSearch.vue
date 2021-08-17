<!-- Product Search component used in the Product Catalogue
  for business admin to search their products -->

<template>
  <page-wrapper>
    <!--    Search Input    -->
    <div class="row form justify-content-center">
      <div class="col-xl-5 col-lg-8 col-md-10 mb-3">
        <div class="form-group row">
          <div class="input-group dropdown">
            <input v-if="productLookup" id="search"
                   v-model="searchTerm"
                   autocomplete="off"
                   class="form-control"
                   data-toggle="dropdown"
                   placeholder="Search products"
                   type="text"
                   @click="showDropdown"
                   @input="searchProductIds"
                   @keyup.enter="search">
            <input v-else
                   v-model="searchTerm"
                   autocomplete="off"
                   class="form-control"
                   placeholder="Search products"
                   type="text"
                   @keyup.enter="search">
            <!-- Autocomplete dropdown, only on CreateInventoryItem -->
            <div v-if="productLookup" id="dropdown" class="dropdown-menu overflow-auto">
              <!-- If no user input -->
              <p v-if="searchTerm.length === 0"
                 class="text-muted dropdown-item left-padding mb-0 disabled"
              >
                Start typing...
              </p>
              <!-- If no matches -->
              <p v-else-if="productSuggestions.length === 0 && searchTerm.length > 0"
                 class="text-muted dropdown-item left-padding mb-0 disabled"
              >
                No results found.
              </p>
              <!-- If there are matches -->
              <a v-for="product in productSuggestions"
                 v-else
                 :key="product.id"
                 class="dropdown-item pointer left-padding"
                 @click="searchById(product.id)">
                <span>{{ product.id }}</span>
              </a>
            </div>
            <div class="input-group-append">
              <button class="btn btn-primary no-outline" type="button" @click="search">Search</button>
            </div>
          </div>
        </div>
        <div v-if="!productLookup" class="form-group row">
          <div class="input-group">
            <div class="input-group-prepend">
              <span class="input-group-text">Search By</span>
            </div>
            <div class="form-control d-flex justify-content-around">
              <div v-for="field in fieldOptions" :key="field.name" class="custom-control custom-checkbox">
                <input :id="field.name" v-model="field.checked"
                       class="custom-control-input" type="checkbox"
                       @click="toggleFieldChecked(field)"
                />
                <label :for="field.name" class="custom-control-label">{{ field.name }}</label>
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
import {Business} from "@/Api";
//import $ from "jquery";

export default {
  name: "ProductSearch",
  components: {
    PageWrapper
  },
  data() {
    return {
      searchTerm: "",
      productSuggestions: [],
      fieldOptions: [
        {
          name: "ID",
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

    /**
     * Checks if the user is on the product catalogue page or the inventory page
     */
    productLookup() {
      return this.$parent.$options._componentTag === "create-inventory-item"
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
     * Called when the user clicks on a product id suggestion
     * Applies search based on the product id clicked
     */
    searchById(productId) {
      this.searchTerm = productId
      this.search()
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
          .then(async (response) => {
            const products = await this.$root.$data.product.addProductCurrencies(response.data, this.currency)
            if (this.$parent.$options._componentTag === "create-inventory-item") {
              //This is for in the product look up in inventory
              this.$parent.applySearch(products)
            } else {
              //This is for the product catalogue
              this.$parent.$parent.applySearch(products)
            }
          })
          .catch((err) => {
            console.log(`There was an error searching products: ${err}`)
          })
    },

    /**
     * Filters autocomplete options (product ids) based on the user's input for a product.
     */
    searchProductIds() {
      if (this.productLookup) {
        //Set ID to true
        this.fieldOptions[0].checked = true
        //Set Name to false
        this.fieldOptions[1].checked = false
        Business.searchProducts(this.businessId, this.searchTerm,
            this.fieldOptions[0].checked,
            this.fieldOptions[1].checked,
            this.fieldOptions[2].checked,
            this.fieldOptions[3].checked,
        )
            .then(async (response) => {
              this.productSuggestions = await this.$root.$data.product.addProductCurrencies(response.data, this.currency)
            })
            .catch((err) => {
              console.log(`There was an error searching products: ${err}`)
            })
      }
    },

    showDropdown() {
      const dropdown = document.getElementById('dropdown')
      if (dropdown.classList.contains('show')) {
        document.getElementById('search').click()
      }
    }
  }
}
</script>

<style scoped>

.fields-title {
  font-size: 18px;
}

</style>