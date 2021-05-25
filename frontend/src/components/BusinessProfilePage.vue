<template>
  <page-wrapper>

    <login-required
        v-if="!isLoggedIn"
        page="view a business's profile page"
    />

    <div v-else>

      <div class="row mb-3">
        <div class="col">
          <div class="row">
            <div class="col-12 text-center mb-2">
              <h2>{{ name }}</h2>
            </div>
          </div>

          <!-- Name of Business -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Name of Business: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 70%">{{ name }}</p>
            </div>
          </div>

          <!-- Description -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Description: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 70%">{{ description }}</p>
            </div>
          </div>

          <!-- Address-->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Address: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 70%">{{ address }}</p>
            </div>
          </div>

          <!-- Type -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Type: </p>
            </div>
            <div class="col-6">
              <p>{{ businessType }}</p>
            </div>
          </div>

          <!-- Date of Registration -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Date of Registration: </p>
            </div>
            <div class="col-6">
              <p>Registered since: {{ dateJoined }} ({{ dateSinceJoin }})</p>
            </div>
          </div>

          <!-- Administrators -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Administrators: </p>
            </div>
            <div class="col-6">
              <table>
                <tr v-for="(admin, index) in administrators" :key="index">
                  <td>
                    <router-link :to="`/users/${admin.id}`" class="nav-link p-0">
                      {{ admin.firstName }} {{ admin.lastName }}
                    </router-link>
                  </td>
                  <td v-if="isPrimaryAdmin && (primaryAdminId !== admin.id)">
                    <p class="nav-link d-inline" style="font-size: 11px; color: red; cursor: pointer;"
                       v-on:click="removeAdministrator(admin.id, admin.firstName, admin.lastName)">Remove</p>
                  </td>
                </tr>
              </table>
            </div>

          </div>

          <div class="row">
            <div v-if="removedAdmin" class="col text-center">
              <p style="color: green">{{ removedAdmin }}</p>
            </div>
            <div v-if="error" class="col text-center">
              <p style="color: red">{{ error }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="row justify-content-center" style="margin-bottom: 10px">
        <router-link :to="`businesses/${this.businessId}/listings`" class="btn btn-outline-primary mx-2">
          View Business's Listings
        </router-link>
      </div>

      <div v-if="$root.$data.user.canDoAdminAction()" class="row justify-content-center">
        <router-link :to="`businesses/${this.businessId}/products`" class="btn btn-outline-danger mr-2">
          View Product Catalogue
        </router-link>
        <router-link :to="`businesses/${this.businessId}/inventory`" class="btn btn-outline-danger mr-2">
          View Inventory
        </router-link>
        <button class="btn btn-outline-danger" data-target="#addAdministrator" data-toggle="modal">Add Administrator
        </button>


        <div id="addAdministrator" aria-hidden="true" aria-labelledby="addAdministratorLabel" class="modal fade"
             role="dialog" tabindex="-1">
          <div class="modal-dialog" role="document">
            <div class="modal-content">
              <div class="modal-header">
                <h5 id="addAdministratorLabel" class="modal-title">Add Administrator</h5>
                <button aria-label="Close" class="close" data-dismiss="modal" type="button">
                  <span aria-hidden="true">&times;</span>
                </button>
              </div>
              <div class="modal-body">
                <div class="form-row">
                  <!--    User ID    -->
                  <label for="userId">
                    <strong>User ID</strong>
                  </label>
                  <br/>
                  <input id="userId" v-model="addAdministratorUserId"
                         :class="{'form-control': true, 'is-invalid': addAdministratorError}"
                         placeholder="ID of the user you want to make administrator"
                         required style="width:100%" type="number">
                  <!--   Error message for userId input   -->
                  <span class="invalid-feedback text-left">{{ addAdministratorError }}</span>

                  <div v-if="addAdministratorSuccess" class="col text-center mb-2">
                    <p style="color: green">{{ addAdministratorSuccess }}</p>
                  </div>

                </div>
              </div>
              <div class="modal-footer">
                <button class="btn btn-block btn-danger" style="width: 40%; margin:0 10px"
                        v-on:click="addAdministrator">
                  Add Administrator
                </button>
                <button class="btn btn-secondary" data-dismiss="modal" type="button">Close</button>
              </div>
            </div>
          </div>
        </div>


      </div>

    </div>

  </page-wrapper>
</template>

<script>

import {Business} from '@/Api';
import LoginRequired from "./LoginRequired";
import PageWrapper from "@/components/PageWrapper";

export default {
  name: "BusinessProfilePage",
  props: {
    msg: String
  },
  mounted() {
    Business.getBusinessData(this.businessId).then((response) => this.profile(response))
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
     * Gets the user ID
     * @returns {any}
     */
    userId() {
      return this.$root.$data.user.state.userId;
    },

    /**
     * Gets the user ID
     * @returns {any}
     */
    primaryAdminId() {
      return this.primaryAdministratorId;
    },

    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /**
     * Computes if the current user is the primary admin
     * @returns {boolean|*}
     */
    isPrimaryAdmin() {
      return this.$root.$data.user.canDoAdminAction() ||
          Number(this.$root.$data.user.state.userId) === this.primaryAdministratorId
    },
  },

  watch: {
    /**
     * Called when the businessId is changed, this occurs when the path variable for the business id is updated
     */
    businessId(value) {
      if (value !== undefined) {
        Business.getBusinessData(value).then((response) => this.profile(response))
      }
    }
  },

  components: {
    PageWrapper,
    LoginRequired
  },
  methods: {

    /**
     * Assigns the data from the response to the profile variables
     * @param response is the response from the server
     */
    profile(response) {
      this.primaryAdministratorId = response.data.primaryAdministratorId
      this.name = response.data.name;
      this.description = response.data.description;
      this.businessType = response.data.businessType;
      this.administrators = response.data.administrators;

      //Uncomment the following statements and remove the two lines above when the home address is an object. Hopefully it works
      this.address = this.$root.$data.address.formatAddress(response.data.address)

      this.dateJoined = response.data.created
      this.timeCalculator(Date.parse(this.dateJoined))
      this.dateJoined = this.dateJoined.substring(0, 10)
    },

    /**
     * Function to remove an administrator from the business.
     * uses the removeAdministrator method in the Business api.js file to send a request to the backend
     * @param userId id of the user removing as admin
     * @param firstName first name of the user removing as admin
     * @param lastName last name of the user removing as admin
     */
    async removeAdministrator(userId, firstName, lastName) {

      try {
        await Business.removeAdministrator(this.$route.params.businessId, userId)
        this.removedAdmin = `Removed ${firstName} ${lastName} from administering business`
        //Reload the data
        Business.getBusinessData(this.businessId).then((response) => this.profile(response))
      } catch (err) {
        this.error = err.response
            ? err.response.data.slice(err.response.data.indexOf(":") + 2)
            : err
      }
    },

    /**
     * Method to add a user with id addAdministratorUserId to the administrators of the business.
     * Used when a GAA or DGAA wants to add a user as admin to the business
     */
    async addAdministrator() {
      this.addAdministratorError = null
      this.addAdministratorSuccess = null
      if (this.addAdministratorUserId === null) {
        this.addAdministratorError = "Please enter a User ID"
      }
      try {
        await Business.addAdministrator(Number(this.businessId), Number(this.addAdministratorUserId))
        this.addAdministratorSuccess = `Added user with id ${this.addAdministratorUserId} to administrators of business with id ${this.businessId}`
        //Reload the data
        Business.getBusinessData(this.businessId).then((response) => this.profile(response))
      } catch (err) {
        this.addAdministratorError = err.response
            ? err.response.data.slice(err.response.data.indexOf(":") + 2)
            : err
      }
    },

    /**
     * Calculates the time since the user joined in ms from the start of time (1970)
     * @param joined date when the user joined
     */
    timeCalculator(joined) {
      let dateNow = new Date();
      const milliYear = 31557600000
      //milliMonths = 30.4 days
      const milliMonth = 2629800000

      let text = ''
      dateNow = Date.parse(dateNow)
      //Calculate time since they have been a member in milliseconds
      let since = dateNow - joined
      let sinceYears = 0
      let sinceMonths = 0

      //Find how many years
      while (since >= milliYear) {
        sinceYears += 1
        since -= milliYear
      }

      //Find how many months
      while (since >= milliMonth) {
        sinceMonths += 1
        since -= milliMonth
      }

      //Format Text
      switch (true) {
        case (sinceYears === 1):
          text = `${sinceYears} year`
          break
        case (sinceYears > 1):
          text = `${sinceYears} years`
          break
      }

      switch (true) {
        case (text === '' && sinceMonths > 1):
          text = `${sinceMonths} months`
          break
        case (text === '' && sinceMonths === 1):
          text = `${sinceMonths} month`
          break
        case (sinceMonths > 1):
          text += ` and ${sinceMonths} months`
          break
        case (sinceMonths === 1):
          text += `and ${sinceMonths} month`
          break
        case (text === '' && sinceMonths === 0):
          text = 'Less than 1 month'
          break
      }
      this.dateSinceJoin = text
    }
  },
  data() {

    return {
      primaryAdministratorId: null,
      name: null,
      description: null,
      businessType: null,
      address: null,
      dateJoined: null,
      dateSinceJoin: null,
      administrators: null,
      removedAdmin: null,
      addAdministratorUserId: null,
      addAdministratorError: null,
      addAdministratorSuccess: null,
      error: null
    }
  }

}

</script>

<style scoped>

</style>