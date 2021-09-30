<template>
  <div>

    <!-- Profile image -->
    <div class="row mb-3">
      <div class="col text-center">
        <img
            :src="getPrimaryImageThumbnail()"
            alt="profile image"
            class="profile-image rounded-left rounded-right"
            style="max-height: 200px"
        />
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
              <router-link :to="`/businesses/${business.id}`" class="nav-link p-0">
                {{ business.name }}
              </router-link>
            </th>
          </tr>
        </table>
      </div>
    </div>

    <!--Button to add as admin to business currently acting as-->
    <div
        v-if="!isViewingSelf && !isAdministrator && userState.isActingAsBusiness() && userState.isPrimaryAdminOfBusiness()"
        class="d-flex justify-content-center"
    >
      <button class="btn btn-primary"
              v-on:click="addAdministrator">Add as Business Administrator
      </button>
    </div>

    <div
        v-if="!isViewingSelf && isAdministrator && userState.isActingAsBusiness() && userState.isPrimaryAdminOfBusiness()"
        class="d-flex justify-content-center"
    >
      <button class="btn btn-danger"
              v-on:click="removeAdministrator">Remove as Business Administrator
      </button>
    </div>

    <div class="row">
      <div v-if="adminMessage" class="col text-center mb-2">
        <br>
        <p class="text-primary">{{ adminMessage }}</p>
        <br>
      </div>
      <div v-if="error" class="col-8 offset-2 text-center mb-2">
        <alert>{{ error }}</alert>
      </div>
    </div>

    <div v-if="userRole === 'defaultGlobalApplicationAdmin'" class="row d-flex justify-content-center">
      <button v-if="isGAA" id="removeGAAButton" class="btn btn-block btn-outline-danger"
              style="width: 15%;margin:0 20px; font-size: 14px;"
              v-on:click="removeUserAdmin(userId)">Remove Admin Access
      </button>
      <button v-else-if="!isDGAA" id="addGAAButton" class="btn btn-block btn-outline-danger"
              style="width: 15%;margin:0 20px; font-size: 14px;"
              v-on:click="addUserAdmin(userId)">Grant Admin Access
      </button>
    </div>

    <hr/>

    <div class="row">
      <div class="col text-center">
        <h4>User's Images</h4>
      </div>
    </div>

    <div v-if="images.length === 0">
      <p class="text-center">This user has no images.</p>
    </div>
    <div v-else class="row" style="height: 500px">
      <div class="col col-12 justify-content-center">
        <div id="imageCarousel" class="carousel slide" data-ride="carousel">
          <div class="carousel-inner">
            <div v-for="(image, index) in images" v-bind:key="image.id"
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

    <hr/>

    <div class="row">
      <div v-if="cards.length !== 0" class="col text-center">
        <h4>User's Cards</h4>
      </div>
    </div>

    <!-- Cards -->
    <div class="row row-cols-1 row-cols-lg-2 mb-3">
      <div v-for="card in cards" v-bind:key="card.id" class="col">
        <MarketCard v-if="!expired(card)" :card-data="card" :hide-image="hideImages"
                    :show-expired="false"></MarketCard>
      </div>
    </div>

  </div>
</template>

<script>
import userState from "@/store/modules/user"
import {Business, Images, User} from '@/Api'
import Alert from "@/components/Alert";
import MarketCard from "../marketplace/MarketCard";

export default {
  name: "Profile",

  components: {
    Alert,
    MarketCard
  },

  props: {
    msg: String,
    userId: Number
  },

  data() {
    return {
      userState: userState,
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
      adminMessage: null,
      error: null,
      cards: [],
      hideImages: true,
    }
  },

  async mounted() {
    await this.getUserData()
    await this.getCardData();
    this.filterCards();
  },

  computed: {
    /**
     * Checks to see if user is logged in currently
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    /**
     * Returns true if the user is primary admin of any businesses
     */
    isPrimaryAdmin() {
      return this.primaryAdminOf.length > 0;
    },

    /**
     * Returns true if the user is currently viewing their profile page
     */
    isViewingSelf() {
      return this.userId.toString() === this.userState.state.userId.toString()
    },

    /**
     * Returns true if the user is an administrator of the currently acting business
     */
    isAdministrator() {
      for (const business of this.businessesAdministered) {
        if (business.id === this.userState.state.actingAs.id) return true
      }
      return false
    },

    /**
     * Gets the currently logged in user's role
     */
    userRole() {
      return this.userState.state.role;
    },

    /**
     * Returns true if user is GAA
     */
    isGAA() {
      return this.role === "globalApplicationAdmin"
    },

    /**
     * Returns true if user is DGAA
     */
    isDGAA() {
      return this.role === "defaultGlobalApplicationAdmin"
    }
  },

  watch: {
    /**
     * Called when the userId is changed, this occurs when the path variable for the user id is updated
     */
    async userId(value) {
      if (value !== undefined) {
        await this.getUserData()
        await this.getCardData()
        this.filterCards()
      }
    }
  },

  methods: {
    /**
     * Assigns the data from the response to the profile variables
     *
     * @param userData the user's data
     */
    profile(userData) {
      this.firstName = userData.firstName
      this.middleName = userData.middleName
      this.lastName = userData.lastName
      this.nickName = userData.nickname
      this.bio = userData.bio
      this.email = userData.email
      this.role = userData.role
      this.images = userData.images
      this.primaryImageId = userData.primaryImageId
      if (this.isViewingSelf) {
        this.dateOfBirth = userData.dateOfBirth
        this.phoneNumber = userData.phoneNumber
      }

      this.homeAddress = this.$root.$data.address.formatAddress(userData.homeAddress)

      this.dateJoined = userData.created
      this.timeCalculator(Date.parse(this.dateJoined))
      this.dateJoined = this.dateJoined.substring(0, 10)
      this.businessesAdministered = userData.businessesAdministered
      this.setPrimaryAdminList()
    },

    /**
     * Uses the primaryImageId of the user to find the primary image and return its imageURL,
     * else it returns the default user image url
     */
    getPrimaryImageThumbnail() {
      if (this.primaryImageId !== null) {
        const primaryImageId = this.primaryImageId
        const filteredImages = this.images.filter(function (specificImage) {
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
        await Business.addAdministrator(Number(this.$root.$data.user.state.actingAs.id), Number(this.$route.params.userId))
        this.adminMessage = `Added ${this.firstName} ${this.lastName} as business administrator`
        //Reload the data
        await this.getUserData()
      } catch (err) {
        this.error = err.response
            ? err.response.data.slice(err.response.data.indexOf(":") + 2)
            : err
      }
    },

    /**
     * Method that removes the user as a business admin for currently acting business.
     */
    async removeAdministrator() {
      try {
        await Business.removeAdministrator(Number(this.$root.$data.user.state.actingAs.id), Number(this.$route.params.userId))
        this.adminMessage = `Removed ${this.firstName} ${this.lastName} as business administrator`
        await this.getUserData()
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
        if (business.primaryAdministratorId === parseInt(this.$route.params.userId)) {
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
          await this.getUserData()
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
          await this.getUserData()
        } catch (error) {
          console.error(error)
          this.error = error.response
              ? error.response.data
              : error
        }
      }
    },

    async getUserData() {
      try {
        const res = await User.getUserData(this.userId)
        this.profile(res.data)
        this.$emit('user-data-obtained', {
          firstName: this.firstName,
          lastName: this.lastName,
          role: this.role
        })
      } catch (error) {
        let message = error.response ? error.response.data.slice(error.response.data.indexOf(":") + 2) : error
        this.error = message.charAt(0).toUpperCase() + message.slice(1)
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
    }

  }
}

</script>

<style>

.carousel-control-next,
.carousel-control-prev {
  filter: invert(100%); /* Changes the button colours to grey so you can see them */
}

</style>
