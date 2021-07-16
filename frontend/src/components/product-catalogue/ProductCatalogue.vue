<template>
  <page-wrapper>

    <login-required
        v-if="!isLoggedIn"
        page="view this business's Product Catalogue"
    />

    <admin-required
        v-else-if="!isAdminOf"
        page="view this business's product catalogue"
    />

    <div v-else class="container-fluid">
      <div class="row justify-content-center">
        <!-- Page Content -->
        <div class="col">

          <!--    Product Catalogue Header    -->
          <div class="row">
            <div class="col"/>
            <div class="col text-center">
              <h4>Product Catalogue</h4>
            </div>
            <div class="col text-right">
              <!--              Button for GAA or DGAA to add product (so the button is red)-->
              <button v-if="$root.$data.user.canDoAdminAction()" class="btn btn-danger" data-target="#createProduct"
                      data-toggle="modal" @click="newProduct">
                New Product
              </button>
              <button v-else class="btn btn-primary" data-target="#createProduct" data-toggle="modal"
                      @click="newProduct">
                New Product
              </button>
            </div>
          </div>

          <product-search ref="productSearch"/>

          <!--    Error Alert    -->
          <div v-if="error" class="row">
            <div class="col text-center">
              <alert>{{ error }}</alert>
            </div>
          </div>

          <catalogue-items ref="catalogueItems" :business-id="this.businessId"
                           :selecting-item="false"></catalogue-items>

        </div>
      </div>
    </div>

    <div id="createProduct" :key="this.createNewProduct" class="modal fade" data-backdrop="static">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-body">
            <create-product @refresh-products="refreshProducts" :products="businessesProducts"></create-product>
          </div>
        </div>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import LoginRequired from '../LoginRequired'
import AdminRequired from "@/components/AdminRequired";
import Alert from '../Alert'
import CreateProduct from "@/components/product-catalogue/CreateProduct";
import CatalogueItems from "@/components/product-catalogue/ProductCatalogueItems";
import PageWrapper from "@/components/PageWrapper";
import ProductSearch from "@/components/product-catalogue/ProductSearch";

export default {
  name: "Catalogue",

  components: {
    ProductSearch,
    PageWrapper,
    CatalogueItems,
    CreateProduct,
    LoginRequired,
    AdminRequired,
    Alert
  },

  data() {
    return {
      error: null,
      createNewProduct: false,
      //This array is used when sending the businesses products to the CreateProduct page,
      // as if you put 'products' in the props, an error is thrown because the $refs arent defined when the page opens
      businessesProducts: []
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
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    products() {
      return this.$refs.catalogueItems.products
    },

    searchedProducts() {
      return this.$refs.productSearch.products
    },

    actor() {
      return this.$root.$data.user.state.actingAs;
    },

    /**
     * Check if the user is an admin of the business and is acting as that business, or is a GAA
     */
    isAdminOf() {
      if (this.$root.$data.user.canDoAdminAction()) return true
      else if (this.actor.type !== "business") return false
      return this.actor.id === parseInt(this.$route.params.businessId);
    },
  },
  watch: {
    /**
     * Updates the table when the user searches for products
     */
    searchedProducts() {
      this.$refs.catalogueItems.products = this.searchedProducts
    }
  },
  methods: {
    /**
     * Takes user to page to create new product.
     */
    newProduct() {
      this.businessesProducts = this.products
      this.createNewProduct = true;
    },

    /**
     * Refreshes the product catalogue, refilling the table.
     */
    refreshProducts() {
      this.createNewProduct = false;
      this.$refs.catalogueItems.fillTable();
    }
  }
}
</script>

<style scoped>

</style>