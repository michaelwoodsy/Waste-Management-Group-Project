<template>
  <div>
    <!-- Displayed if not logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="Add a New Listing"
    />

    <!-- Check if the user is an admin of the business -->
    <admin-required
        v-else-if="!isAdminOfBusiness"
        :page="`of the business ${this.businessId} to make a listing under this business`"
    />

    <div v-else class="container-fluid"> <!--    If Logged In    -->
      <br><br>

      <!-- Display error message if there is one -->
      <div class="row">
        <div class="col-12 col-sm-8 offset-sm-2">
          <alert v-if="errorMessage">Error: {{ errorMessage }}</alert>
        </div>
      </div>

        <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">

          <div class="col-12 text-center mb-2">
            <h2>Add a New Listing</h2>
          </div>

          <!-- Name of Inventory Item -->
          <div class="form-row mb-3" style="margin-top:20px">
            <b>Inventory Item: </b>
          </div>

          <!-- Quantity -->
          <div class="form-row mb-3">
            <b>Quantity: </b>
          </div>

          <!-- Price-->
          <div class="form-row mb-3">
            <b>Price: </b>
          </div>

          <!-- Closing Date -->
          <div class="form-row mb-3">
            <b>Closing Date: </b>
          </div>

          <div class="form-row mb-3">
            <!--    More Info    -->
            <label class="description-label" for="description">
              <b>More Info: </b>
            </label>
            <br>
            <textarea id="description" maxlength="255" v-model="description"
                      class="form-control" placeholder="Write additional listing information (Max length 255 characters)"
                      style="width: 100%; height: 200px;">

            </textarea>
          </div>
          <div class="form-row mb-3">
            <button class="btn btn-block btn-primary" style="width: 100%; margin:0 20px"
                    v-on:click="checkInputs">
              Add Listing
            </button>
          </div>

        </div>
    </div>
  </div>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import AdminRequired from "@/components/AdminRequired";
import Alert from "@/components/Alert";
import {Business} from "@/Api";

/**
 * Default starting parameters
 */
export default {
  name: "ListItemPage",
  mounted() {
    this.loadItem();
    this.getCurrency()
    Business.getProducts(this.$route.params.businessId).then((response) => this.getProductIds(response))
  },

  components: {
    AdminRequired,
    LoginRequired,
    Alert
  },

  data() {
    return {
      id: '', // Required
    };
  },

  computed: {
    /** Checks to see if user is logged in currently **/
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /** Gets the business ID that is in the current path **/
    businessId() {
      return this.$route.params.businessId;
    },

    /** Gets the inventory item ID that is in the current path **/
    inventoryItemId() {
      return this.$route.params.inventoryItemId;
    },

    /** A list of businesses the current user administers **/
    businessesAdministered() {
      if (this.isLoggedIn) {
        return this.$root.$data.user.state.userData.businessesAdministered;
      }
      return []
    },

    /** Checks if the user is an admin of the business **/
    isAdminOfBusiness() {
      let isAdmin = false;
      // iterate over each business they administer and check if they administer this one
      this.businessesAdministered.forEach((business) => {
        if (business.id.toString() === this.businessId.toString()) {
          isAdmin = true;
        }
      })
      return isAdmin
    },
  },

}
</script>

<style scoped>

</style>