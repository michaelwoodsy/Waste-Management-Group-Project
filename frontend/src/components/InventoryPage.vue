<template>
  <div>

    <login-required
        v-if="!isLoggedIn"
        page="view an inventory"
    />

    <admin-required
        v-else-if="!isAdminOf()"
        page="view this business's inventory"
    />

    <div v-else class="container-fluid">
      <div class="row justify-content-center">
        <div class="col">

          <!--    Inventory Header    -->
          <div class="row">
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
              <button v-else class="btn btn-primary" data-target="#createInventoryItem" data-toggle="modal" @click="newItem">
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
      <div ref="createInventoryItemWindow" class="modal-dialog modal-open">
        <div class="modal-content">
          <div class="modal-body">
            <create-inventory-item @refresh-inventory="refreshInventory"></create-inventory-item>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";
import Alert from "@/components/Alert";
import CreateInventoryItem from "@/components/CreateInventoryItem";
import InventoryItems from "@/components/InventoryItems";

export default {
  name: "InventoryPage",

  components: {
    InventoryItems,
    CreateInventoryItem,
    LoginRequired,
    AdminRequired,
    Alert
  },

  data() {
    return {
      error: null,
      createNewInventoryItem: false
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

<style scoped>

</style>