<template>
  <page-wrapper>
    <div class="row">
      <div class="col text-center mb-2">
        <h2>{{ firstName }} {{ lastName }}
          <span v-if="isGAA && canDoAdminAction" class="badge badge-danger admin-badge">ADMIN</span>
          <span v-else-if="isDGAA && canDoAdminAction" class="badge badge-danger admin-badge">DGAA</span>
        </h2>
      </div>
    </div>


    <!-- Profile image -->
    <div class="row">
      <div class="col text-center mb-2">
        <img
            alt="profile image"
            class="profile-image rounded-left rounded-right"
            style="max-width: 200px"
            :src="getPrimaryImage()"
        />
      </div>
    </div>

    <div class="row d-flex justify-content-center">
      <div class="col text-center m-3">
        <!-- Edit button -->
        <router-link v-if="isViewingSelf" @click="close" data-dismiss="modal"
                     :to="`users/${userId}/edit`" class="btn btn-primary">Edit Profile</router-link>
        <router-link v-else-if="canDoAdminAction" @click="close" data-dismiss="modal"
              :to="`users/${userId}/edit`" class="btn btn-danger">Edit User</router-link>
      </div>
    </div>

    <!-- First Name -->
    <div class="row">
      <div class="col-6 text-right font-weight-bold">
        <p>First Name: </p>
      </div>
      <div class="col-6">
        <p style="word-wrap: break-word; max-width: 70%">{{ firstName }} </p>
      </div>
    </div>

    <!-- Middle Name -->
    <div class="row">
      <div class="col-6 text-right font-weight-bold">
        <p>Middle Name: </p>
      </div>
      <div class="col-6">
        <p style="word-wrap: break-word; max-width: 70%">{{ middleName }} </p>
      </div>
    </div>

    <!-- Last Name -->
    <div class="row">
      <div class="col-6 text-right font-weight-bold">
        <p>Last Name: </p>
      </div>
      <div class="col-6">
        <p style="word-wrap: break-word; max-width: 70%">{{ lastName }} </p>
      </div>
    </div>

    <!-- Nickname -->
    <div class="row">
      <div class="col-6 text-right font-weight-bold">
        <p>Nickname: </p>
      </div>
      <div class="col-6">
        <p style="word-wrap: break-word; max-width: 70%">{{ nickName }} </p>
      </div>
    </div>

    <!-- Bio -->
    <div class="row">
      <div class="col-6 text-right font-weight-bold">
        <p>Bio: </p>
      </div>
      <div class="col-6">
        <p style="word-wrap: break-word; max-width: 70%">{{ bio }} </p>
      </div>
    </div>

    <!-- Email -->
    <div class="row">
      <div class="col-6 text-right font-weight-bold ">
        <p>Email: </p>
      </div>
      <div class="col-6">
        <p style="word-wrap: break-word; max-width: 70%">{{ email }} </p>
      </div>
    </div>

    <!-- Home Address -->
    <div class="row">
      <div class="col-6 text-right font-weight-bold">
        <p>Location: </p>
      </div>
      <div class="col-6">
        <p style="word-wrap: break-word; max-width: 70%">{{ homeAddress }} </p>
      </div>
    </div>

    <!--Information about the user only they should see-->
    <div v-if="isViewingSelf">
      <!-- Phone -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold ">
          <p>Phone: </p>
        </div>
        <div class="col-6">
          <p style="word-wrap: break-word; max-width: 70%">{{ phoneNumber }} </p>
        </div>
      </div>

      <!-- Date of Birth -->
      <div class="row">
        <div class="col-6 text-right font-weight-bold ">
          <p>Date of Birth: </p>
        </div>
        <div class="col-6">
          <p style="word-wrap: break-word; max-width: 70%">{{ dateOfBirth }} </p>
        </div>
      </div>
    </div>

    <!-- Member Since -->
    <div class="row">
      <div class="col-6 text-right font-weight-bold">
        <p>Member Since: </p>
      </div>
      <div class="col-6">
        <p>{{ dateJoined }} ({{ dateSinceJoin }}) </p>
      </div>
    </div>

    <!-- Primary Admin to Business links -->
    <div v-if="isPrimaryAdmin" class="row">
      <div class="col-6 text-right font-weight-bold">
        <p>Primary Administrator of: </p>
      </div>
      <div class="col-6">
        <table aria-label="Businesses this user administers">
          <tr v-for="(business, index) in primaryAdminOf" :key="index">
            <th id="business-name" class="font-weight-normal">
              <router-link data-dismiss="modal" :to="`/businesses/${business.id}`" class="nav-link p-0">
                {{ business.name }}
              </router-link>
            </th>
          </tr>
        </table>
      </div>
    </div>

    <!--Button to add as admin to business currently acting as-->
    <div
        v-if="!isViewingSelf && !isAdministrator && this.$root.$data.user.isActingAsBusiness() && this.$root.$data.user.isPrimaryAdminOfBusiness()"
        class="d-flex justify-content-center">
      <button class="btn btn-block btn-secondary" style="width: 40%;margin:0 20px; font-size: 14px;"
              v-on:click="addAdministrator">Add as administrator to business
      </button>
    </div>

    <div class="row">
      <div v-if="addedAdmin" class="col text-center mb-2">
        <br>
        <p style="color: green">{{ addedAdmin }}</p>
        <br>
      </div>
      <div v-if="error" class="col-8 offset-2 text-center mb-2">
        <alert>{{ error }}</alert>
      </div>
    </div>

    <div v-if="userRole === 'defaultGlobalApplicationAdmin'" class="row d-flex justify-content-center">
      <button v-if="isGAA" id="removeGAAButton" class="btn btn-block btn-danger"
              style="width: 15%;margin:0 20px; font-size: 14px;"
              v-on:click="removeUserAdmin(userId)">Remove Admin Access
      </button>
      <button v-else-if="!isDGAA" id="addGAAButton" class="btn btn-block btn-success"
              style="width: 15%;margin:0 20px; font-size: 14px;"
              v-on:click="addUserAdmin(userId)">Grant Admin Access
      </button>
    </div>

    <div class="row">
      <div class="col text-left mb-2">
        <h2>User's Images</h2>
      </div>
    </div>

    <div v-if="images.length === 0">
      <p class="text-center"><strong>This User has no Images</strong></p>
    </div>
    <div v-else class="row" style="height: 500px">
      <div class="col col-12 justify-content-center">
        <div id="imageCarousel" class="carousel slide" data-ride="carousel">
          <div class="carousel-inner">
            <div v-for="(image, index) in images" v-bind:key="image.id"
                 :class="{'carousel-item': true, 'active': index === 0}">
              <img class="d-block img-fluid rounded mx-auto d-block" style="height: 500px" :src="getImageURL(image.filename)" alt="User Image">
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

    <div class="row">
      <div class="col text-left mb-2" v-if="cards.length !== 0">
        <h2>User's Cards</h2>
      </div>
    </div>

    <!-- Cards -->
    <div class="row row-cols-1 row-cols-lg-2 mb-3">
      <div v-for="card in cards" v-bind:key="card.id" class="col">
        <MarketCard :card-data="card" :hide-image="hideImages" :show-expired="false" v-if="!expired(card)"></MarketCard>
      </div>
    </div>
  </page-wrapper>

</template>

<script>
import Alert from "@/components/Alert";
import PageWrapper from "@/components/PageWrapper";
import MarketCard from "../marketplace/MarketCard";
import {Business, Images, User} from "@/Api";
export default {
  name: "ProfilePageModal",
  components: {
    PageWrapper,
    Alert,
    MarketCard
  },
  props: {
    id: Number
  },
  data() {
    return {
      firstName: null,
      middleName: null,
      lastName: null,
      nickName: null,
      bio: null,
      email: null,
      homeAddress: null,
      dateOfBirth: null,
      phoneNumber: null,
      dateJoined: null,
      dateSinceJoin: null,
      role: null,
      images: [],
      primaryImageId: null,
      businessesAdministered: [],
      primaryAdminOf: [],
      addedAdmin: null,
      error: null,
      cards: [],
      hideImages: true
    }
  },
  watch: {
    async id() {
      await this.getData()
    }
  },
  async mounted() {
    await this.getData()
  },
  computed: {
    /**
     * Gets the users' ID
     * @returns {any}
     */
    userId() {
      return this.id
    },

    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /**
     * Returns true if the user is primary admin of any businesses
     * @returns {boolean|*}
     */
    isPrimaryAdmin() {
      return this.primaryAdminOf.length > 0;
    },

    /**
     * Returns true if the user is currently viewing their profile page
     * @returns {boolean|*}
     */
    isViewingSelf() {
      return this.userId.toString() === this.$root.$data.user.state.userId.toString()
    },

    /**
     * Returns true if the user is an administrator of the curentley acting business
     * @returns {boolean|*}
     */
    isAdministrator() {
      for (const business of this.businessesAdministered) {
        if (business.id  === this.$root.$data.user.state.actingAs.id) return true
      }
      return false
    },
    /**
     * Gets the currently logged in user's role
     */
    userRole() {
      return this.$root.$data.user.state.role;
    },

    isGAA() {
      return this.role === "globalApplicationAdmin"
    },

    isDGAA() {
      return this.role === "defaultGlobalApplicationAdmin"
    },
    canDoAdminAction() {
      return this.$root.$data.user.canDoAdminAction()
    }
  },
  methods: {
    /**
     * Retrieves the data for a user
     */
    async getData() {
      User.getUserData(this.id).then((response) => this.profile(response))
      await this.getCardData();
      this.filterCards();
    },
    /**
     * Assigns the data from the response to the profile variables
     * @param response is the response from the server
     */
    profile(response) {
      this.firstName = response.data.firstName
      this.middleName = response.data.middleName
      this.lastName = response.data.lastName
      this.nickName = response.data.nickname
      this.bio = response.data.bio
      this.email = response.data.email
      this.role = response.data.role
      this.images = response.data.images
      this.primaryImageId = response.data.primaryImageId
      if (this.isViewingSelf) {
        this.dateOfBirth = response.data.dateOfBirth
        this.phoneNumber = response.data.phoneNumber
      }

      this.homeAddress = this.$root.$data.address.formatAddress(response.data.homeAddress)

      this.dateJoined = response.data.created
      this.timeCalculator(Date.parse(this.dateJoined))
      this.dateJoined = this.dateJoined.substring(0, 10)
      this.businessesAdministered = response.data.businessesAdministered
      this.setPrimaryAdminList()
    },

    /**
     * Uses the primaryImageId of the user to find the primary image and return its imageURL,
     * else it returns the default user image url
     */
    getPrimaryImage() {
      if (this.primaryImageId !== null) {
        const primaryImageId = this.primaryImageId
        const filteredImages = this.images.filter(function(specificImage) {
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
     * Function to add an administrator to the currently acting as business.
     * uses the addAdministrator method in the Business api.js file to send a request to the backend
     */
    async addAdministrator() {
      try {
        await Business.addAdministrator(Number(this.$root.$data.user.state.actingAs.id), this.id)
        this.addedAdmin = `Added ${this.firstName} ${this.lastName} to administrators of business`
        //Reload the data
        User.getUserData(this.userId).then((response) => this.profile(response))
      } catch (err) {
        this.error = err.response
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
    },

    /**
     * Creates list of businesses that user is primary admin of
     */
    setPrimaryAdminList() {
      this.primaryAdminOf = []
      for (const business of this.businessesAdministered) {
        if (business.primaryAdministratorId === this.id) {
          this.primaryAdminOf.push(business);
        }
      }
    },

    /**
     * Calls api end point to add admin rights to a user
     */
    async addUserAdmin(id) {
      if (this.userRole !== 'defaultGlobalApplicationAdmin') {
        this.error = 'You must be a global admin to grant admin rights to a user'
      } else {
        try {
          await User.makeAdmin(id)
          console.log(`Successfully granted admin rights to user with id ${id}`)
          const response = await User.getUserData(id)
          this.profile(response)
        } catch (error) {
          console.error(error)
          this.error = error.response
              ? error.response.data
              : error
        }
      }
    },

    /**
     * Calls api end point to remove admin rights from a user
     */
    async removeUserAdmin(id) {
      if (this.userRole !== 'defaultGlobalApplicationAdmin') {
        this.error = 'You must be a global admin to revoke admin rights from a user'
      } else {
        try {
          await User.revokeAdmin(id)
          console.log(`Successfully revoked admin rights from user with id ${id}`)
          const response = await User.getUserData(id)
          this.profile(response)
        } catch (error) {
          console.error(error)
          this.error = error.response
              ? error.response.data
              : error
        }
      }
    },
    /**
     * Gets the user's cards to display on their profile page
     */
    async getCardData() {
      try {
        const response = await User.getCards(this.userId)
        this.cards = response.data
      } catch (error) {
        console.error(error)
        this.error = error.response ? error.response.data : error
      }
    },
    expired(card) {
      const now = new Date();
      if (now >= new Date(card.displayPeriodEnd)) {
        return true;
      }
    },
    filterCards() {
      this.cards = this.cards.filter((card) => {
        return !this.expired(card);
      })
    },
    /**
     * Closes the modal
     */
    close() {
      this.$emit("close-profile")
    }
  }
}
</script>

<style scoped>

</style>