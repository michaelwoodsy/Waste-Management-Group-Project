<template>
  <page-wrapper>
    <div class="row mb-3">
      <div class="col">
        <div class="row">
          <div class="col-12 text-center mb-2">
            <h2>{{ name }}</h2>
          </div>
        </div>

        <!-- Profile image -->
        <div class="row">
          <div class="col-12 text-center mb-2">
            <img
                alt="profile"
                class="profile-image rounded-circle"
                style="max-width: 300px"
                :src="getImageURL('/media/defaults/defaultProfile.jpg')"
            />
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
                  <router-link data-dismiss="modal" :to="`/users/${admin.id}`" class="nav-link p-0">
                    {{ admin.firstName }} {{ admin.lastName }}
                  </router-link>
                </td>
              </tr>
            </table>
          </div>
        </div>
      </div>
    </div>

    <div class="row justify-content-center" style="margin-bottom: 10px">
      <router-link data-dismiss="modal" :to="`businesses/${this.businessId}/listings`" class="btn btn-outline-primary mx-2">
        View Business's Listings
      </router-link>
    </div>

    <div class="row">
      <div class="col text-left mb-2">
        <h2>Businesses Images</h2>
      </div>
    </div>

    <div class="row" style="height: 500px">
      <div class="col col-12 justify-content-center">
        <div id="imageCarousel" class="carousel slide" data-ride="carousel">
          <div class="carousel-inner">
            <!--   Image 1   -->
            <div class="carousel-item active">
              <img class="d-block img-fluid rounded mx-auto d-block" style="height: 500px" :src="getImageURL('/media/defaults/defaultProfile.jpg')" alt="User Image">
            </div>
            <!--   Image 2   -->
            <div class="carousel-item">
              <img class="d-block img-fluid rounded mx-auto d-block" style="height: 500px" :src="getImageURL('/media/defaults/defaultProfile.jpg')" alt="User Image">
            </div>
          </div>
          <a class="carousel-control-prev" href="#imageCarousel" role="button" data-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="sr-only">Previous</span>
          </a>
          <a class="carousel-control-next" href="#imageCarousel" role="button" data-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="sr-only">Next</span>
          </a>
        </div>
      </div>
    </div>
  </page-wrapper>
</template>

<script>
import PageWrapper from "@/components/PageWrapper";
import {Business, Images} from "@/Api";
export default {
  name: "BusinessProfilePageModal",
  components: {
    PageWrapper
  },
  props: {
    id: Number
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
  },
  mounted() {
    console.log(this.id)
    Business.getBusinessData(this.id).then((response) => this.profile(response))
  },
  computed: {
    /**
     * Gets the business ID
     * @returns {any}
     */
    businessId() {
      return this.id;
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
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
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
}
</script>

<style scoped>

</style>