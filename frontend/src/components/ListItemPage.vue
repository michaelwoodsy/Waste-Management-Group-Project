<template>
  <div>
    <!-- Displayed if not logged in -->
    <login-required
        v-if="!isLoggedIn"
        page="Add a New Listing"
    />

    <!-- Displayed if not admin of business -->
    <admin-required
        v-else-if="!isAdminOf"
        page="Add a New Listing"
    />

    <div v-else class="container-fluid"> <!--    If Logged In    -->
      <br><br>

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
import AdminRequired from "@/components/AdminRequired";
import LoginRequired from "@/components/LoginRequired";

/**
 * Default starting parameters
 */
export default {
  name: "ListItemPage",
  components: {
    AdminRequired,
    LoginRequired
  },

  data() {
    return {
      id: '', // Required
    };
  },

  computed: {
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },
    /**
     * Checks to see if the user is acting as the current business.
     */
    isAdminOf() {
      if (this.$root.$data.user.state.actingAs.type !== 'business') return false;
      return this.$root.$data.user.state.actingAs.id === parseInt(this.$route.params.businessId);
    }
  },

}
</script>

<style scoped>

</style>