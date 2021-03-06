<template>

  <div v-if="userState.isLoggedIn()" class="container-fluid">

    <!--    Search Users Header    -->
    <div class="row">
      <div class="col-12 text-center mb-2">
        <h4>Search Users</h4>
      </div>
    </div>

    <!--    Error Alert    -->
    <div v-if="error" class="row justify-content-center">
      <div class="col-8 text-center mb-2">
        <alert>{{ error }}</alert>
      </div>
    </div>

    <!--    Search Input    -->
    <div class="row justify-content-center mb-3">
      <div class="col col-sm-8 col-lg-5">
        <div class="input-group">
          <input id="search"
                 v-model="searchTerm"
                 :class="{'is-invalid': searchError}"
                 class="form-control"
                 placeholder="Name / Nickname"
                 type="search"
                 @keyup.enter="search">
          <div class="input-group-append">
            <button class="btn btn-primary" type="button" @click="search">Search</button>
          </div>
        </div>
        <span class="invalid-feedback d-block text-center">{{ searchError }}</span>
      </div>
    </div>

    <!--    Result Information    -->
    <div class="row justify-content-center">
      <div class="col">
        <!--    Order By   -->
        <div class="overflow-auto">
          <table aria-label="Table showing user search results"
                 class="table table-hover"
          >
            <thead>
            <tr>
              <!--    User Image    -->
              <th id="userImage"></th>

              <!--    First Name    -->
              <th class="pointer" scope="col" @click="orderSearch('firstName')">
                <p class="d-inline">First Name</p>
                <p v-if="orderCol === 'firstName'" class="d-inline">{{ orderDirArrow }}</p>
              </th>

              <!--    Middle Name    -->
              <th class="pointer" scope="col" @click="orderSearch('middleName')">
                <p class="d-inline">Middle Name</p>
                <p v-if="orderCol === 'middleName'" class="d-inline">{{ orderDirArrow }}</p>
              </th>

              <!--    Last Name    -->
              <th class="pointer" scope="col" @click="orderSearch('lastName')">
                <p class="d-inline">Last Name</p>
                <p v-if="orderCol === 'lastName'" class="d-inline">{{ orderDirArrow }}</p>
              </th>

              <!--    Email    -->
              <th class="pointer" scope="col" @click="orderSearch('email')">
                <p class="d-inline">Email</p>
                <p v-if="orderCol === 'email'" class="d-inline">{{ orderDirArrow }}</p>
              </th>

              <!--    Home Address    -->
              <th class="pointer" scope="col" @click="orderSearch('homeAddress')">
                <p class="d-inline">Address</p>
                <p v-if="orderCol === 'homeAddress'" class="d-inline">{{ orderDirArrow }}</p>
              </th>
            </tr>
            </thead>
            <!--    User Information    -->
            <tbody v-if="!loading">
            <tr v-for="user in users"
                v-bind:key="user.id"
                class="pointer"
                data-target="#viewUserModal"
                data-toggle="modal"
                @click="viewUser(user.id)"
            >
              <td>
                <img :src="getPrimaryImageThumbnail(user)"
                     alt="userImage">
              </td>
              <td>
                {{ nameAndNickname(user) }}<br>
                <span v-if="userState.canDoAdminAction() && user.role === 'globalApplicationAdmin'"
                      class="badge badge-danger admin-badge">ADMIN</span>
                <span v-else-if="userState.canDoAdminAction() && user.role === 'defaultGlobalApplicationAdmin'"
                      class="badge badge-danger admin-badge">DGAA</span>
              </td>
              <td>{{ user.middleName }}</td>
              <td>{{ user.lastName }}</td>
              <td>{{ user.email }}</td>
              <td>{{ formattedAddress(user.homeAddress) }}</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div v-if="loading" class="row">
      <div class="col-12 text-center">
        <p class="text-muted">Loading...</p>
      </div>
    </div>

    <!--    Result Information    -->
    <div class="row">
      <div class="col">
        <div class="mb-2 text-center">
          <showing-results-text
              :items-per-page="resultsPerPage"
              :page="page"
              :total-count="totalCount"
          />
        </div>
        <pagination
            :current-page.sync="page"
            :items-per-page="resultsPerPage"
            :total-items="totalCount"
            class="mx-auto"
            @change-page="changePage"
        />
      </div>
    </div>

    <profile-page-modal v-if="viewUserModal" :id="viewUserModalId" @close-profile="getUserData"/>

  </div>

  <login-required v-else page="search users"/>

</template>

<script>
import {Images, User} from '@/Api'
import LoginRequired from '../LoginRequired'
import ShowingResultsText from "../ShowingResultsText";
import Pagination from "../Pagination";
import Alert from '../Alert'
import ProfilePageModal from "@/components/user/ProfilePageModal";
import userState from "@/store/modules/user"

export default {
  name: "UserSearch",
  components: {
    ProfilePageModal,
    LoginRequired,
    Alert,
    ShowingResultsText,
    Pagination
  },

  data() {
    return {
      userState: userState,
      searchTerm: "",
      searchError: null,
      users: [],
      viewUserModal: false,
      viewUserModalId: null,
      error: null,
      orderCol: null,
      orderDirection: false, // False -> Ascending
      resultsPerPage: 10,
      totalCount: 0,
      page: 1,
      loading: false
    }
  },

  mounted() {
    this.search()
  },

  computed: {
    /**
     * Checks which direction (ascending or descending) the order by should be
     * @returns {string}
     */
    orderDirArrow() {
      if (this.orderDirection) {
        return '↓'
      }
      return '↑'
    },

    /**
     * Returns the sortBy string for search request
     */
    sortBy() {
      if (this.orderCol === null) return ""
      let sortBy = this.orderCol
      if (!this.orderDirection) {
        sortBy += "ASC"
      } else {
        sortBy += "DESC"
      }
      return sortBy
    }
  },

  methods: {
    /**
     * Search Logic
     */
    async search() {
      this.blurSearch();
      this.users = [];
      this.loading = true;
      this.page = 1;
      this.orderCol = null
      this.orderDirection = false
      await this.getUserData()
    },
    /**
     * Function is called by pagination component to make another call to the backend
     * to update the list of users that should be displayed
     */
    async changePage(page) {
      this.blurSearch();
      this.loading = true;
      this.page = page
      await this.getUserData()
    },
    /**
     * Function called when you click on one of the columns to order the results
     * Makes another call to the backend to get the correct users when ordered
     */
    async orderSearch(sortBy) {
      this.blurSearch();
      this.loading = true;

      if (this.orderCol !== sortBy) {
        this.orderDirection = false
      } else {
        this.orderDirection = !this.orderDirection
      }

      this.orderCol = sortBy
      await this.getUserData()
    },

    /**
     * Gets user data
     */
    async getUserData() {
      this.viewUserModal = false
      try {
        const res = await User.getUsers(this.searchTerm, this.page - 1, this.sortBy)
        this.error = null;
        this.users = res.data[0];
        this.totalCount = res.data[1];
        this.loading = false;
      } catch (error) {
        this.error = error;
        this.loading = false;
      }
    },

    /**
     * Uses the primaryImageId of the user to find the primary image and return its imageURL,
     * else it returns the default user image url
     */
    getPrimaryImageThumbnail(user) {
      if (user.primaryImageId === null) {
        return this.getImageURL('/media/defaults/defaultProfile_thumbnail.jpg')
      }
      const filteredImages = user.images.filter(function (specificImage) {
        return specificImage.id === user.primaryImageId;
      })
      if (filteredImages.length === 1) {
        return this.getImageURL(filteredImages[0].thumbnailFilename)
      }
      //Return the default image if the program gets to this point (if it does something went wrong)
      return this.getImageURL('/media/defaults/defaultProfile_thumbnail.jpg')
    },

    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
    },

    /**
     * Blurs the search.
     */
    blurSearch() {
      document.getElementById('search').blur()
    },

    /**
     * Formats address of user by using their home address object
     * @param address object that stores the users home address
     * @returns {string}
     */
    formattedAddress(address) {
      return this.$root.$data.address.formatAddress(address)
    },

    /**
     * Router link to the clicked users profile page
     * @param id
     */
    viewUser(id) {
      this.viewUserModalId = id
      this.viewUserModal = true
    },

    /**
     * concatenates the users first name and nickname (if they have one)
     * @param user
     * @returns {string|null|string|*}
     */
    nameAndNickname(user) {
      if (user.nickname) {
        return `${user.firstName} (${user.nickname})`
      }
      return user.firstName
    }
  }
}
</script>

<style scoped>

.pointer {
  cursor: pointer;
}

</style>
