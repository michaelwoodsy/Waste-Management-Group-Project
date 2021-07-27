<!--
BusinessSearch.vue
Component on Search page for searching businesses
-->


<template>
  <page-wrapper>

    <div v-if="isLoggedIn" class="container-fluid">

      <!--    Search Businesses Header    -->
      <div class="row">
        <div class="col-12 text-center mb-2">
          <h4>Search Businesses</h4>
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
        <div class="col-sm-3"></div>
        <div class="col-sm-5">
          <div class="input-group">
            <input id="search"
                   v-model="searchTerm"
                   class="form-control no-outline"
                   :class="{'is-invalid': searchError}"
                   placeholder="business name"
                   type="search"
                   @keyup.enter="search">
            <div class="input-group-append">
              <button class="btn btn-primary no-outline" type="button" @click="search">Search</button>
            </div>
          </div>
          <span class="invalid-feedback d-block text-center">{{ searchError }}</span>
        </div>
        <!--    Select business type    -->
        <div class="col-sm-4">
          <select id="businessType" v-model="businessType" class="form-control"
                  required style="width:100%" type="text">
            <option disabled hidden selected value>Filter by business type</option>
            <!--    'Any type' option to choose not to filter by business type    -->
            <option>Any type</option>
            <option>Accommodation and Food Services</option>
            <option>Retail Trade</option>
            <option>Charitable organisation</option>
            <option>Non-profit organisation</option>
          </select>
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
                   aria-label="Table showing business search results">
              <thead>
              <tr>
                <!--    ID    -->
                <th class="pointer" scope="col" @click="orderSearch('id')">
                  <p class="d-inline">Id</p>
                  <p v-if="orderCol === 'id'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Business Image    -->
                <th id="businessImage"></th>

                <!--    First Name    -->
                <th class="pointer" scope="col" @click="orderSearch('name')">
                  <p class="d-inline">Name</p>
                  <p v-if="orderCol === 'name'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Business Type    -->
                <th class="pointer" scope="col" @click="orderSearch('businessType')">
                  <p class="d-inline">Type</p>
                  <p v-if="orderCol === 'businessType'" class="d-inline">{{ orderDirArrow }}</p>
                </th>

                <!--    Address    -->
                <th class="pointer" scope="col" @click="orderSearch('address')">
                  <p class="d-inline">Address</p>
                  <p v-if="orderCol === 'address'" class="d-inline">{{ orderDirArrow }}</p>
                </th>
              </tr>
              </thead>

              <!--    Business Information    -->
              <tbody v-if="!loading">
              <tr v-for="business in businesses"
                  v-bind:key="business.id"
                  class="pointer"
                  data-target="#viewBusinessModal"
                  data-toggle="modal"
                  @click="viewBusiness(business.id)"
              >
                <th scope="row">
                  {{ business.id }}
                </th>
                <td>
                  <img alt="businessImage"
                       :src="getPrimaryImageThumbnail(business)">
                </td>
                <td>{{ business.name }}</td>
                <td>{{ business.businessType }}</td>
                <td>{{ formattedAddress(business.address) }}</td>
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
    </div>

    <div v-if="viewBusinessModal" id="viewBusinessModal" class="modal fade" data-backdrop="static">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-body">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close" @click="viewBusinessModal=false">
              <span aria-hidden="true">&times;</span>
            </button>
            <business-profile-page-modal :id="viewBusinessModalId"></business-profile-page-modal>
          </div>
        </div>
      </div>
    </div>

  </page-wrapper>
</template>

<script>
import ShowingResultsText from "@/components/ShowingResultsText";
import PageWrapper from "@/components/PageWrapper";
import Alert from "@/components/Alert";
import Pagination from "@/components/Pagination";
import {Images, Business} from "@/Api";
import BusinessProfilePageModal from "@/components/business/BusinessProfilePageModal";

export default {
  name: "BusinessSearch",
  components: {
    BusinessProfilePageModal,
    PageWrapper,
    ShowingResultsText,
    Alert,
    Pagination
  },
  data() {
    return {
      searchTerm: "",
      searchError: null,
      businessType: "",
      businesses: [],
      viewBusinessModal: false,
      viewBusinessModalId: null,
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
     * Check if user is logged in
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
    }
  },
  methods: {
    /**
     * This is the search logic, that handles a call with or without businessType
     */
    search() {
      this.blurSearch()
      this.businesses = []
      this.loading = true;
      this.page = 1
      this.orderCol = null
      this.orderDirection = false
      if (this.businessType === 'Any type' || this.businessType === '') {
        this.businessType = null;
      }
      Business.getBusinesses(this.searchTerm, this.businessType, this.page-1, "")
          .then((res) => {
            this.error = null;
            this.businesses = res.data[0]
            this.totalCount = res.data[1]
            this.loading = false;
            console.log(this.businesses)
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })
    },

    /**
     * Function is called by pagination component to make another call to the backend
     * to update the list of businesses that should be displayed
     */
    async changePage() {
      this.blurSearch();
      this.loading = true;
      let sortBy = this.orderCol
      if (!this.orderDirection){
        sortBy += "ASC"
      } else {
        sortBy += "DESC"
      }
      if (this.orderCol === null) sortBy = ""
      await Business.getBusinesses(this.searchTerm, this.businessType, this.page-1, sortBy)
          .then((res) => {
            this.error = null;
            this.businesses = res.data[0];
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
     * Makes another call to the backend to get the correct businesses when ordered
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
      await Business.getBusinesses(this.searchTerm, this.businessType, this.page-1, sortBy)
          .then((res) => {
            this.error = null;
            this.businesses = res.data[0];
            this.totalCount = res.data[1];
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
     * Formats address of business by using their address object
     * @param address object that stores the business' home address
     * @returns {string}
     */
    formattedAddress(address) {
      return this.$root.$data.address.formatAddress(address)
    },

    /**
     * Turns popup modal to view  business on
     * @param id
     */
    viewBusiness(id) {
      this.viewBusinessModalId = id
      this.viewBusinessModal = true
    },

    /**
     * Uses the primaryImageId of the business to find the primary image and return its imageURL,
     * else it returns the default business image url
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
  }
}
</script>

<style scoped>

.pointer {
  cursor: pointer;
}

</style>