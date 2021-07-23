<template>
  <page-wrapper>

    <div v-if="isLoggedIn" class="container-fluid">

      <!--    Search Users Header    -->
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h4>Search Users</h4>
        </div>
      </div>

      <!--    Error Alert    -->
      <div v-if="error" class="row">
        <div class="col-8 offset-2 text-center mb-2">
          <alert>{{ error }}</alert>
        </div>
      </div>

      <!--    Search Input    -->
      <div class="row mb-2">
        <div class="col-12 col-sm-8 col-lg-6 col-xl-4 col-centered">
          <div class="input-group">
            <input id="search"
                   v-model="searchTerm"
                   class="form-control no-outline"
                   :class="{'is-invalid': searchError}"
                   placeholder="name/nickname"
                   type="search"
                   @keyup.enter="search">
            <div class="input-group-append">
              <button class="btn btn-primary no-outline" type="button" @click="search">Search</button>
            </div>
          </div>
          <span class="invalid-feedback d-block text-center">{{ searchError }}</span>
        </div>
      </div>

      <!--    Result Information    -->
      <div class="row justify-content-center">
        <div class="col-12">
          <div class="text-center">
            <showing-results-text
                :items-per-page="resultsPerPage"
                :page="page"
                :total-count="totalCount"
            />
          </div>

          <!--    Order By   -->
          <div class="overflow-auto">
            <table class="table table-hover"
                   aria-label="Table showing user search results"
            >
              <thead>
              <tr>
                <!--    ID    -->
                <th class="pointer" scope="col" @click="orderSearch('id')">
                  <p class="d-inline">Id</p>
                  <p v-if="orderCol === 'id'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    User Image    -->
                <th id="userImage"></th>

                <!--    First Name    -->
                <th class="pointer" scope="col" @click="orderSearch('firstName')">
                  <p class="d-inline">Firstname</p>
                  <p v-if="orderCol === 'firstName'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Middle Name    -->
                <th class="pointer" scope="col" @click="orderSearch('middleName')">
                  <p class="d-inline">Middlename</p>
                  <p v-if="orderCol === 'middleName'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Last Name    -->
                <th class="pointer" scope="col" @click="orderSearch('lastName')">
                  <p class="d-inline">Lastname</p>
                  <p v-if="orderCol === 'lastName'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Email    -->
                <th class="pointer" scope="col" @click="orderSearch('email')">
                  <p class="d-inline">Email</p>
                  <p v-if="orderCol === 'email'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Home Address    -->
                <th class="pointer" scope="col" @click="orderSearch('homeAddress')" >
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
                <th scope="row">
                  {{ user.id }}
                  <span v-if="isActingAsAdmin && user.role === 'globalApplicationAdmin'"
                        class="badge badge-danger admin-badge">ADMIN</span>
                  <span v-else-if="isActingAsAdmin && user.role === 'defaultGlobalApplicationAdmin'"
                        class="badge badge-danger admin-badge">DGAA</span>
                </th>
                <td>
                  <img alt="productImage"
                       :src="getPrimaryImageThumbnail(user)">
                </td>
                <td>{{ nameAndNickname(user) }}</td>
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
        <div class="col-12">
          <pagination
              :current-page.sync="page"
              :items-per-page="resultsPerPage"
              :total-items="totalCount"
              class="mx-auto"
          />
        </div>
      </div>

      <div v-if="viewUserModal" id="viewUserModal" class="modal fade" data-backdrop="static">
        <div class="modal-dialog modal-xl">
          <div class="modal-content">
            <div class="modal-body">
              <button type="button" class="close" data-dismiss="modal" aria-label="Close" @click="viewUserModal=false">
                <span aria-hidden="true">&times;</span>
              </button>
              <profile-page-modal @close-profile="viewUserModal=false" :id="viewUserModalId"></profile-page-modal>
            </div>
          </div>
        </div>
      </div>
    </div>

    <login-required v-else page="search users"/>

  </page-wrapper>
</template>

<script>
import {Images, User} from '@/Api'
import LoginRequired from '../LoginRequired'
import ShowingResultsText from "../ShowingResultsText";
import Pagination from "../Pagination";
import Alert from '../Alert'
import PageWrapper from "@/components/PageWrapper";
import ProfilePageModal from "@/components/user/ProfilePageModal";

export default {
  name: "UserSearch",
  components: {
    ProfilePageModal,
    PageWrapper,
    LoginRequired,
    Alert,
    ShowingResultsText,
    Pagination
  },

  data() {
    return {
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

  computed: {
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*}
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

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
     * Returns whether the currently logged in user is the DGAA
     * @returns {boolean|*}
     */
    isActingAsAdmin() {
      return this.$root.$data.user.canDoAdminAction()
    }
  },

  methods: {
    /**
     * Search Logic
     */
    search() {
      this.blurSearch();
      this.users = [];
      this.loading = true;
      this.page = 1;
      this.orderCol = null
      this.orderDirection = false

      User.getUsers(this.searchTerm, this.page-1, "firstNameASC")
          .then((res) => {
            this.error = null;
            this.users = res.data[0];
            this.totalCount = res.data[1];
            this.loading = false;
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })
    },
    /**
     * Function is called by pagination component to make another call to the backend
     * to update the list of users that should be displayed
     */
    async changePage() {
      this.blurSearch();
      this.loading = true;
      let sortBy = this.orderCol
      if(!this.orderDirection){
        sortBy += "ASC"
      } else {
        sortBy += "DESC"
      }
      await User.getUsers(this.searchTerm, this.page - 1, sortBy)
          .then((res) => {
            this.error = null;
            this.users = res.data[0];
            this.totalCount = res.data[1];
            this.loading = false;
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })
    },
    /**
     * Function called when you click on one of the columns to order the results
     * Makes another call to the backend to get the correct users when ordered
     */
    async orderSearch(sortBy) {
      this.blurSearch();
      this.loading = true;

      if(this.orderCol !== sortBy){
        this.orderDirection = false
      } else {
        this.orderDirection = !this.orderDirection
      }

      this.orderCol = sortBy
      if(!this.orderDirection){
        sortBy += "ASC"
      } else {
        sortBy += "DESC"
      }
      await User.getUsers(this.searchTerm, this.page - 1, sortBy)
          .then((res) => {
            this.error = null;
            this.users = res.data[0];
            this.totalCount = res.data[1];
            this.loading = false;
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })
    },

    /**
     * Uses the primaryImageId of the product to find the primary image and return its imageURL,
     * else it returns the default product image url
     */
    getPrimaryImageThumbnail(user) {
      if (user.primaryImageId === null) {
        return this.getImageURL('/media/defaults/defaultProfile_thumbnail.jpg')
      }
      const filteredImages = user.images.filter(function(specificImage) {
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
     * Function to order search results by specific column
     * @param col column to be sorted by
     */
    orderResults(col) {
      // Remove the ordering if the column is clicked and the arrow is down
      if (this.orderCol === col && this.orderDirection) {
        this.orderCol = null;
        this.orderDirection = false;
        return
      }

      // Updated order direction if the new column is the same as what is currently clicked
      this.orderDirection = this.orderCol === col;
      this.orderCol = col;
    },

    // Function for sorting a list by orderCol alphabetically
    sortAlpha(a, b) {
      if (a[this.orderCol] === null) {
        return -1
      }
      if (b[this.orderCol] === null) {
        return 1
      }
      if (a[this.orderCol] < b[[this.orderCol]]) {
        return 1;
      }
      if (a[this.orderCol] > b[[this.orderCol]]) {
        return -1;
      }
      return 0;
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
.col-centered {
  margin: 0 auto;
  float: none;
}

.pointer {
  cursor: pointer;
}

</style>
