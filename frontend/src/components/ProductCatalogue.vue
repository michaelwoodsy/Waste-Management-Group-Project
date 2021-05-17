<template>
  <div>

    <login-required
        v-if="!isLoggedIn"
        page="view this business's Product Catalogue"
    />

    <admin-required
        v-else-if="!isAdminOf()"
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
<!--              Buton for GAA or DGAA to add product (so the button is red)-->
              <button v-if="$root.$data.user.canDoAdminAction()" class="btn btn-danger" data-target="#createProduct"
                      data-toggle="modal" @click="newProduct">
                New Product
              </button>
              <button v-else class="btn btn-primary" data-target="#createProduct" data-toggle="modal" @click="newProduct">
                New Product
              </button>
            </div>
          </div>

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
            <create-product @refresh-products="refreshProducts"></create-product>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import LoginRequired from './LoginRequired'
import AdminRequired from "@/components/AdminRequired";
import Alert from './Alert'
import CreateProduct from "@/components/CreateProduct";
import CatalogueItems from "@/components/ProductCatalogueItems";

export default {
  name: "Catalogue",

  components: {
    CatalogueItems,
    CreateProduct,
    LoginRequired,
    AdminRequired,
    Alert
  },

  data() {
    return {
      error: null,
      createNewProduct: false
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

    actor() {
      return this.$root.$data.user.state.actingAs;
    }
  },

  methods: {
    /**
     * Check if the user is an admin of the business and is acting as that business
     */
    isAdminOf() {
      if (this.$root.$data.user.canDoAdminAction()) return true
      else if (this.actor.type !== "business") return false
      return this.actor.id === parseInt(this.$route.params.businessId);
    },

    productIdExists(id) {
      for (const product of this.products) {
        if (product.id === id) {
          return true;
        }
      }
      return false;
    },

    /**
     * Takes user to page to create new product.
     */
    newProduct() {
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