<template>
  <div>
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
                   placeholder="name or nickname"
                   type="search"
                   @keyup.enter="search">
            <div class="input-group-append">
              <button class="btn btn-primary no-outline" type="button" @click="search">Search</button>
            </div>
          </div>
        </div>
      </div>

      <!--    Result Information    -->
      <div class="row">
        <div class="d-none d-lg-block col-lg-1"/>
        <div class="col-12 col-lg-10">
          <div class="text-center">
            <showing-results-text
                :items-per-page="resultsPerPage"
                :page="page"
                :total-count="totalCount"
            />
          </div>

          <!--    Order By   -->
          <div class="overflow-auto">
            <table class="table table-hover">
              <thead>
              <tr>
                <!--    ID    -->
                <th class="pointer" scope="col" @click="orderResults('id')">
                  <p class="d-inline">Id</p>
                  <p v-if="orderCol === 'id'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    First Name    -->
                <th class="pointer" scope="col" @click="orderResults('firstName')">
                  <p class="d-inline">Firstname</p>
                  <p v-if="orderCol === 'firstName'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Middle Name    -->
                <th class="pointer" scope="col" @click="orderResults('middleName')">
                  <p class="d-inline">Middlename</p>
                  <p v-if="orderCol === 'middleName'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Last Name    -->
                <th class="pointer" scope="col" @click="orderResults('lastName')">
                  <p class="d-inline">Lastname</p>
                  <p v-if="orderCol === 'lastName'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Email    -->
                <th class="pointer" scope="col" @click="orderResults('email')">
                  <p class="d-inline">Email</p>
                  <p v-if="orderCol === 'email'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Home Address    -->
                <th class="pointer" scope="col" @click="orderResults('homeAddress')">
                  <p class="d-inline">Address</p>
                  <p v-if="orderCol === 'homeAddress'" class="d-inline">{{ orderDirArrow }}</p>
                </th>
              </tr>
              </thead>
              <!--    User Information    -->
              <tbody v-if="!loading">
              <tr v-for="user in paginatedUsers"
                  v-bind:key="user.id"
                  class="pointer"
                  @click="viewUser(user.id)"
              >
                <th scope="row">
                  {{ user.id }}
                  <span class="badge badge-danger admin-badge" v-if="isActingAsAdmin && user.role === 'globalApplicationAdmin'">ADMIN</span>
                  <span class="badge badge-danger admin-badge" v-else-if="isActingAsAdmin && user.role === 'defaultGlobalApplicationAdmin'">DGAA</span>
                </th>
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
        <div class="d-none d-lg-block col-lg-1"/>
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
    </div>

    <login-required v-else page="search users"/>
  </div>
</template>

<script>
import {User} from '@/Api'
import LoginRequired from './LoginRequired'
import ShowingResultsText from "./ShowingResultsText";
import Pagination from "./Pagination";
import Alert from './Alert'

export default {
  name: "UserSearch",
  components: {
    LoginRequired,
    Alert,
    ShowingResultsText,
    Pagination
  },

  data() {
    return {
      searchTerm: "",
      users: [],
      error: null,
      orderCol: null,
      orderDirection: false, // False -> Ascending
      resultsPerPage: 10,
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
     * Sort Users Logic
     * @returns {[]|*[]}
     */
    sortedUsers() {
      if (this.orderCol === null) {
        return this.users
      }

      // Create new users array and sort
      let newUsers = [...this.users];
      // Order direction multiplier for sorting
      const orderDir = (this.orderDirection ? 1 : -1);

      // Sort users if there are any
      if (newUsers.length > 0) {
        // Sort users
        newUsers.sort((a, b) => {
          return orderDir * this.sortAlpha(a, b)
        });
      }

      return newUsers
    },

    /**
     * Paginate the users
     * @returns {*[]|*[]}
     */
    paginatedUsers() {
      let newUsers = this.sortedUsers;

      // Sort users if there are any
      if (newUsers.length > 0) {
        // Splice the results to showing size
        const startIndex = this.resultsPerPage * (this.page - 1);
        const endIndex = this.resultsPerPage * this.page;
        newUsers = newUsers.slice(startIndex, endIndex)
      }

      return newUsers
    },

    /**
     * Calculates the number of results in users array
     * @returns {number}
     */
    totalCount() {
      return this.users.length
    },
    /**
     * Returns whether the currentley logged in user is the DGAA
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

        User.getUsers(this.searchTerm)
            .then((res) => {
              this.error = null;
              this.users = res.data;
              this.loading = false;
            })
            .catch((err) => {
              this.error = err;
              this.loading = false;
            })
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
      this.$router.push({name: 'viewUser', params: {userId: id}})
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
