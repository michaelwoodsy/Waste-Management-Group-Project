<template>
  <page-wrapper>

    <login-required
        v-if="!isLoggedIn"
        page="view an inventory"
    />

    <admin-required
        v-else-if="!isAdminOf"
        page="view this business's inventory"
    />

    <div v-else class="container-fluid">
      <div class="row justify-content-center">
        <div class="col">

          <!--    Inventory Header    -->
          <div class="row mb-3">
            <div class="col"/>
            <div class="col text-center">
              <h4>Inventory</h4>
            </div>
            <div class="col text-right">
              <!--              Buton for GAA or DGAA to add product (so the button is red)-->
              <button v-if="$root.$data.user.canDoAdminAction()" class="btn btn-danger"
                      data-target="#createInventoryItem" data-toggle="modal" @click="newItem">
                New Product
              </button>
              <button v-else class="btn btn-primary" data-target="#createInventoryItem" data-toggle="modal"
                      @click="newItem">
                New Item
              </button>
            </div>
          </div>

          <!--    Error Alert    -->
          <div v-if="error" class="row">
            <div class="col text-center">
              <alert>{{ error }}</alert>
            </div>
          </div>

          <inventory-items ref="inventoryItems" :business-id="businessId" :selecting-item="false"></inventory-items>

        </div>
      </div>
    </div>

    <div id="createInventoryItem" :key="this.createNewInventoryItem" class="modal fade" data-backdrop="static">
      <div ref="createInventoryItemWindow" class="modal-dialog modal-open" :class="{'modal-xl': selectingProduct}">
        <div class="modal-content">
          <div class="modal-body">
            <create-inventory-item @refresh-inventory="refreshInventory"
                                   @select-product-toggle="selectingProduct = !selectingProduct"></create-inventory-item>
          </div>
        </div>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";
import Alert from "@/components/Alert";
import CreateInventoryItem from "@/components/inventory/CreateInventoryItem";
import InventoryItems from "@/components/inventory/InventoryItems";
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "InventoryPage",

  components: {
    PageWrapper,
    InventoryItems,
    CreateInventoryItem,
    LoginRequired,
    AdminRequired,
    Alert
  },

  data() {
    return {
      error: null,
      createNewInventoryItem: false,
      selectingProduct: false
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

    inventoryItems() {
      return this.$refs.inventoryItems.inventoryItems;
    },

    actor() {
      return this.$root.$data.user.state.actingAs;
    },

    /**
     * Check if the user is an admin of the business and is acting as that business
     */
    isAdminOf() {
      if (this.$root.$data.user.canDoAdminAction()) return true
      else if (this.actor.type !== "business") return false
      return this.actor.id === parseInt(this.$route.params.businessId);
    },
  },
  methods: {
    /**
     * Takes user to page to create new inventory item.
     */
    newItem() {
      this.createNewInventoryItem = true;
    },

    /**
     * Refreshes the inventory page, refilling the table.
     */
    refreshInventory() {
      this.createNewInventoryItem = false;
      this.$refs.inventoryItems.fillTable();
    }
  }
}
</script>
