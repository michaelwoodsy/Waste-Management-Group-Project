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

    <!-- Featured listings -->
    <div>
      <div class="row">
        <div class="col text-center">
          <h4>Featured Listings</h4>
        </div>
      </div>

      <!-- Display message if there are no featured listings -->
      <div v-if="featuredListings.length === 0">
        <p id="featuredListingText" class="text-center">This business has no featured listings.</p>
      </div>

      <!-- Otherwise, display featured listings in carousel -->
      <div v-else class="row">
        <div class="col-12 col-md-12 col-lg-6 m-auto">
          <div id="carouselExampleControls" v-bind:key="this.featuredListings.length" class="carousel slide"
               data-ride="carousel">

            <!-- Carousel items -->
            <div class="carousel-inner">
              <div v-for="listing in featuredListings"
                   v-bind:key="listing.id"
                   :class="{active: listing === featuredListings[0]}"
                   class="carousel-item p-3"
              >
                <sale-listing
                    :listing-data="listing"
                    style="height: 350px"
                    @close-modal="$emit('close-modal')"
                    @un-feature-listing="removeFromFeatured(listing.id)"
                />
              </div>
            </div>

            <!-- Carousel left button -->
            <a class="carousel-control-prev my-auto"
               data-slide="prev"
               href="#carouselExampleControls"
               role="button"
               style="height: 20px;"
            >
              <span aria-hidden="true" class="carousel-control-prev-icon"></span>
              <span class="sr-only">Previous</span>
            </a>

            <!-- Carousel right button -->
            <a class="carousel-control-next my-auto"
               data-slide="next"
               href="#carouselExampleControls"
               role="button"
               style="height: 20px;">
              <span aria-hidden="true" class="carousel-control-next-icon"></span>
              <span class="sr-only">Next</span>
            </a>
          </div>
        </div>

      </div>
    </div>

    <hr/>

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

        <div class="row justify-content-center mb-3 row-cols-1 row-cols-lg-4">
          <div class="col col-xl-2 mb-2">
            <router-link :to="`businesses/${this.business.id}/listings`" class="btn btn-primary btn-block text-truncate"
                         data-dismiss="modal">
              Sale Listings
            </router-link>
          </div>
          <div v-if="isBusinessAdmin || user.canDoAdminAction()" class="col col-xl-2 mb-2">
            <router-link :to="`businesses/${this.business.id}/products`"
                         class="btn btn-primary btn-block text-truncate">
              Product Catalogue
            </router-link>
          </div>
          <div v-if="isBusinessAdmin || user.canDoAdminAction()" class="col col-xl-2 mb-2">
            <router-link :to="`businesses/${this.business.id}/inventory`"
                         class="btn btn-primary btn-block text-truncate">
              Inventory
            </router-link>
          </div>
          <div v-if="isPrimaryAdmin || user.canDoAdminAction()" class="col col-xl-2 mb-2">
            <button
                :data-target="`#addAdmin${business.id}`" class="btn btn-primary btn-block text-truncate"
                data-toggle="collapse">
              Add Administrator
            </button>
          </div>
        </div>

        <div class="row justify-content-center">
          <div :id="`addAdmin${business.id}`" class="collapse">
            <div class="card shadow" style="max-width: 500px; width: 90vw">
              <div class="card-header">
                <span>Add Administrator</span>
                <button :data-target="`#addAdmin${business.id}`" class="close" data-toggle="collapse">&times;</button>
              </div>
              <div class="card-body">
                <div class="form-row">
                  <div :class="{'is-invalid': addAdministratorError, 'is-valid': addAdministratorSuccess}"
                       class="input-group">
                    <input id="userId" v-model="addAdministratorUserId"
                           :class="{'is-invalid': addAdministratorError, 'is-valid': addAdministratorSuccess}"
                           class="form-control" min="1"
                           placeholder="User ID" required type="number">
                    <div class="input-group-append">
                      <button class="btn btn-block btn-danger"
                              v-on:click="addAdministrator">
                        Add User
                      </button>
                    </div>
                  </div>
                  <span class="invalid-feedback">{{ addAdministratorError }}</span>
                  <span class="valid-feedback">{{ addAdministratorSuccess }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Average Star rating -->
        <div class="row d-flex justify-content-center">
          <em :class="{'bi-star-fill': business.averageRating, 'bi-star': !business.averageRating}"
              class="icon bi float-right"
              style="color: gold; font-size: 30px"/>
          <p v-if="business.averageRating" class="rating-message">{{ business.averageRating.toFixed(2) }}</p>
          <p v-else class="rating-message"> No Ratings</p>
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
                <td v-if="(!readOnly && isPrimaryAdmin && (business.primaryAdministratorId !== admin.id)) ||
                user.canDoAdminAction() && (business.primaryAdministratorId !== admin.id)">
                  <p class="nav-link d-inline" style="font-size: 11px; color: red; cursor: pointer;"
                     v-on:click="removeAdministrator(admin.id, admin.firstName, admin.lastName)">Remove</p>
                </td>
              </tr>
            </table>
          </div>

        </div>

        <div class="row">
          <div v-if="removedAdmin" class="col text-center">
            <p class="text-primary">{{ removedAdmin }}</p>
          </div>
          <div v-if="error" class="col text-center text-danger">
            <p>{{ error }}</p>
          </div>
        </div>
      </div>
    </div>

    <hr/>

    <!-- Images -->
    <div>
      <div class="row">
        <div class="col text-center">
          <h4>Business' Images</h4>
        </div>
      </div>
      <div v-if="business.images.length === 0">
        <p class="text-center">This business has no images.</p>
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

    <!-- Reviews -->
    <div>
      <hr/>
      <business-reviews :business-id="business.id"/>
    </div>

  </div>
</template>

<script>
import userState from "@/store/modules/user";
import {Business, Images} from '@/Api';
import SaleListing from "@/components/sale-listing/SaleListing";
import BusinessReviews from "@/components/business/BusinessReviews";
import $ from 'jquery'

export default {
  name: "BusinessProfile",
  components: {BusinessReviews, SaleListing},
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
  mounted() {
    $(`#addAdmin${this.business.id}`).on('hidden.bs.collapse', () => {
      this.addAdministratorError = null
      this.addAdministratorSuccess = null
      this.addAdministratorUserId = null
    })
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

      return this.getImageURL('/media/defaults/defaultBusinessProfile.jpg')
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
        await Business.removeAdministrator(Number(this.business.id), userId)
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
      if (this.addAdministratorUserId === null || this.addAdministratorUserId < 1) {
        this.addAdministratorError = "Please enter a valid User ID (must be greater than 0)"
      } else {
        try {
          await Business.addAdministrator(Number(this.business.id), Number(this.addAdministratorUserId))
          this.addAdministratorSuccess = `Added user with ID ${this.addAdministratorUserId} as business administrator`
          //Reload the data
          this.$emit('update-data')
        } catch (err) {
          let message = err.response ? err.response.data.slice(err.response.data.indexOf(":") + 2) : err
          this.addAdministratorError = message.charAt(0).toUpperCase() + message.slice(1)
        }
      }
    },

    /**
     * Retrieves the business' featured listings, and sets to  this.featuredListings.
     */
    async getFeaturedListings() {
      let res = await Business.getFeaturedListings(this.business.id)
      this.featuredListings = res.data
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
    },

    /**
     * Removes listing with ID from this.featuredListings.
     * Does nothing if the listing doesn't exist in list.
     * @param listingId ID of listing
     */
    removeFromFeatured(listingId) {
      this.featuredListings = this.featuredListings.filter(listing => listingId !== listing.id);
    }
  }
}

</script>

<style scoped>

.rating-message {
  margin: 13px 0 0 10px;
}

</style>