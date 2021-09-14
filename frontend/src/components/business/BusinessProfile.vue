<!--
Component for displaying a business' information.

-----------------------------------------------------------
Props
-----------------------------------------------------------
business:         Object, required. Business object from backend.
isBusinessAdmin:  Boolean, default false. Displays buttons / options that are only
                  visible to a business admin.
isPrimaryAdmin:   Boolean, default false. Displays buttons / options only available to
                  the businesses primary admin.
readOnly:         Boolean, default true.
-->

<template>
  <div class="container-fluid">

    <div class="row mb-3">
      <div class="col">
        <!-- Profile image -->
        <div class="row mb-3">
          <div class="col-12 text-center">
            <img
                :src="getPrimaryImage()"
                alt="profile"
                class="profile-image rounded-left rounded-right"
                style="max-height: 200px"
            />
          </div>
        </div>

        <!-- Name of Business -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Name of Business: </p>
          </div>
          <div class="col-6">
            <p style="word-wrap: break-word; max-width: 70%">{{ business.name }}</p>
          </div>
        </div>

        <!-- Description -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Description: </p>
          </div>
          <div class="col-6">
            <p style="word-wrap: break-word; max-width: 70%">{{ business.description }}</p>
          </div>
        </div>

        <!-- Address-->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Address: </p>
          </div>
          <div class="col-6">
            <p style="word-wrap: break-word; max-width: 70%">{{ formatAddress(business.address) }}</p>
          </div>
        </div>

        <!-- Type -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Type: </p>
          </div>
          <div class="col-6">
            <p>{{ business.businessType }}</p>
          </div>
        </div>

        <!-- Date of Registration -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Date of Registration: </p>
          </div>
          <div class="col-6">
            <p>Registered since: {{ business.created.substring(0, 10) }} ({{ timeCalculator(business.created) }})</p>
          </div>
        </div>

        <!-- Administrators -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Administrators: </p>
          </div>
          <div class="col-6">
            <table aria-label="Administrators of this business">
              <tr v-for="(admin, index) in business.administrators" :key="index">
                <th id="administrator-name" class="font-weight-normal">
                  <router-link :to="`/users/${admin.id}`" class="nav-link p-0">
                    {{ admin.firstName }} {{ admin.lastName }}
                  </router-link>
                </th>
                <td v-if="!readOnly && isPrimaryAdmin && (business.primaryAdministratorId !== admin.id)">
                  <p class="nav-link d-inline" style="font-size: 11px; color: red; cursor: pointer;"
                     v-on:click="removeAdministrator(admin.id, admin.firstName, admin.lastName)">Remove</p>
                </td>
              </tr>
            </table>
          </div>

        </div>

        <div class="row">
          <div v-if="removedAdmin" class="col text-center text-success">
            <p>{{ removedAdmin }}</p>
          </div>
          <div v-if="error" class="col text-center text-danger">
            <p>{{ error }}</p>
          </div>
        </div>
      </div>
    </div>

    <div class="row justify-content-center mb-3">
      <router-link :to="`businesses/${this.business.id}/listings`" class="btn btn-primary mx-1"
                   data-dismiss="modal">
        View Business' Listings
      </router-link>
      <div v-if="!readOnly && (isBusinessAdmin || user.canDoAdminAction())">
        <router-link
            :class="{'btn-primary': isBusinessAdmin, 'btn-outline-danger': !isBusinessAdmin && user.canDoAdminAction()}"
            :to="`businesses/${this.business.id}/products`" class="btn mx-1">
          View Product Catalogue
        </router-link>
        <router-link
            :class="{'btn-primary': isBusinessAdmin, 'btn-outline-danger': !isBusinessAdmin && user.canDoAdminAction()}"
            :to="`businesses/${this.business.id}/inventory`" class="btn mx-1">
          View Inventory
        </router-link>
        <button v-if="isPrimaryAdmin || user.canDoAdminAction()"
                :class="{'btn-primary': isPrimaryAdmin, 'btn-outline-danger': !isPrimaryAdmin && user.canDoAdminAction()}"
                class="btn mx-1" data-target="#addAdministrator"
                data-toggle="modal">
          Add Administrator
        </button>

        <!-- Add admin modal -->
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

    <!-- Featured listings -->
    <div>
      <div class="row">
        <div class="col text-left mb-2">
          <h2>Featured Listings</h2>
        </div>
      </div>
      <div v-if="featuredListings.length === 0">
        <p class="text-center"><strong>This Business has no featured listings</strong></p>
      </div>
      <div v-else class="row">
        <div
            v-for="listing in featuredListings"
            v-bind:key="listing.id"
            class="col col-6 justify-content-center">
          <sale-listing
              :listing-data="listing"
          />


        </div>
      </div>
    </div>

    <!-- Images -->
    <div>
      <div class="row">
        <div class="col text-left mb-2">
          <h2>Business' Images</h2>
        </div>
      </div>
      <div v-if="business.images.length === 0">
        <p class="text-center"><strong>This Business has no Images</strong></p>
      </div>
      <div v-else class="row" style="height: 500px">
        <div class="col col-12 justify-content-center">
          <div id="imageCarousel" class="carousel slide" data-ride="carousel">
            <div class="carousel-inner">
              <div v-for="(image, index) in business.images" v-bind:key="image.id"
                   :class="{'carousel-item': true, 'active': index === 0}">
                <img :src="getImageURL(image.filename)" alt="User Image"
                     class="d-block img-fluid rounded mx-auto d-block" style="height: 500px">
              </div>
            </div>
            <a class="carousel-control-prev" data-slide="prev" href="#imageCarousel" role="button">
              <span aria-hidden="true" class="carousel-control-prev-icon"></span>
              <span class="sr-only">Previous</span>
            </a>
            <a class="carousel-control-next" data-slide="next" href="#imageCarousel" role="button">
              <span aria-hidden="true" class="carousel-control-next-icon"></span>
              <span class="sr-only">Next</span>
            </a>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import userState from "@/store/modules/user";
import {Business, Images} from '@/Api';
import SaleListing from "@/components/sale-listing/SaleListing";

export default {
  name: "BusinessProfile",
  components: {SaleListing},
  props: {
    business: {
      type: Object,
      required: true
    },
    isBusinessAdmin: {
      type: Boolean,
      required: false,
      default: false
    },
    isPrimaryAdmin: {
      type: Boolean,
      required: false,
      default: false
    },
    readOnly: {
      type: Boolean,
      required: false,
      default: true
    }
  },
  data() {
    return {
      user: userState,
      removedAdmin: null,
      addAdministratorUserId: null,
      addAdministratorError: null,
      addAdministratorSuccess: null,
      error: null,
      canDoAdminAction: false,
      featuredListings: []
    }
  },
  mounted () {
    this.getFeaturedListings()
  },
  methods: {
    /**
     * Returns a formatted address
     */
    formatAddress(address) {
      return this.$root.$data.address.formatAddressWithStreet(address)
    },

    /**
     * Uses the primaryImageId of the business to find the primary image and return its imageURL,
     * else it returns the default business image url
     */
    getPrimaryImage() {
      if (this.business.primaryImageId !== null) {
        const primaryImageId = this.business.primaryImageId
        const filteredImages = this.business.images.filter(function (specificImage) {
          return specificImage.id === primaryImageId;
        })
        if (filteredImages.length === 1) {
          return this.getImageURL(filteredImages[0].filename)
        }
      }

      return this.getImageURL('/media/defaults/defaultProfile.jpg')
    },

    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
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
        this.$emit('update-data')
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
        await Business.addAdministrator(Number(this.business.id), Number(this.addAdministratorUserId))
        this.addAdministratorSuccess = `Added user with id ${this.addAdministratorUserId} to administrators of business with id ${this.businessId}`
        //Reload the data
        this.$emit('update-data')
      } catch (err) {
        this.addAdministratorError = err.response
            ? err.response.data.slice(err.response.data.indexOf(":") + 2)
            : err
      }
    },

    /**
     * Retrieves the business' featured listings, and sets to  this.featuredListings.
     */
    async getFeaturedListings() {
      let res = await Business.searchSaleListings("",
            true,
            false,
            false,
            false,
            null,
            null,
            null,
            null,
            0,
            "bestMatch")
      console.log(res.data)
      this.featuredListings = res.data[0]
    },

    /**
     * Calculates the time since the user joined in ms from the start of time (1970)
     * @param joined date when the user joined
     */
    timeCalculator(joined) {
      joined = Date.parse(joined)
      let dateNow = new Date();
      const milliYear = 31557600000
      //milliMonths = 30.4 days
      const milliMonth = 2629800000

      let text = ''
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
      return text
    }
  }
}

</script>

<style scoped>

</style>