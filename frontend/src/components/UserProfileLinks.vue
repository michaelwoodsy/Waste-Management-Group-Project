<!-- User thumbnail and name for choosing who to act as in navbar -->

<template>
  <div class="nav-item">
    <!-- Image and name -->
    <div class="nav-link text-white pointer" data-toggle="dropdown">
      <!-- Profile photo -->
      <img
          :src="getPrimaryImageThumbnail(actor.type)"
          alt="profile"
          class="profile-image rounded-circle"
      />
      <!-- Users name -->
      <span>{{ actorName }}</span>
    </div>

    <!-- Dropdown menu when name is clicked -->
    <div class="dropdown-menu dropdown-menu-left dropdown-menu-sm-right">

      <!-- Change to business account menu -->
      <div v-if="businessAccounts.length > 0">
        <h6 class="dropdown-header">Businesses</h6>
        <a
            v-for="business in businessAccounts"
            v-bind:key="business.id"
            class="dropdown-item pointer"
            @click="actAsBusiness(business)"
        >
          <img :src="getPrimaryImageThumbnail('business', business.images, business.primaryImageId)" alt="profile"
               class="profile-image-sm rounded-circle">
          {{ business.name }}
        </a>
        <div class="dropdown-divider"/>
      </div>

      <!-- Change to user account menu -->
      <div v-if="userAccounts.length > 0">
        <h6 class="dropdown-header">Users</h6>
        <a
            v-for="user in userAccounts"
            v-bind:key="user.id"
            class="dropdown-item pointer"
            @click="actAsUser(user)"
        >
          <img :src="getPrimaryImageThumbnail('user', user.images, user.primaryImageId)" alt="profile"
               class="profile-image-sm rounded-circle">
          {{ user.firstName }} {{ user.lastName }}
          <span v-if="isGAA" class="badge badge-danger admin-badge">ADMIN</span>
          <span v-else-if="isDGAA" class="badge badge-danger admin-badge">DGAA</span>

        </a>
        <div class="dropdown-divider"/>
      </div>

      <!-- Profile and logout section -->
      <div v-if="actor.type === 'business'">
        <router-link :to="`/businesses/${actor.id}`" class="dropdown-item">Business Profile</router-link>
        <router-link :to="`businesses/${actor.id}/products`" class="dropdown-item">Product Catalogue</router-link>
        <router-link :to="`businesses/${actor.id}/inventory`" class="dropdown-item">Product Inventory</router-link>
        <router-link :to="`businesses/${actor.id}/listings`" class="dropdown-item">Sale Listings</router-link>
        <router-link :to="`businesses/${actor.id}/edit`" class="dropdown-item">Edit Business</router-link>
      </div>
      <div v-else>
        <router-link :to="`/users/${actor.id}/purchases`" class="dropdown-item">My Purchases</router-link>
        <router-link class="dropdown-item" to="/businesses">Create Business</router-link>
        <router-link :to="`users/${actor.id}`" class="dropdown-item">My Profile</router-link>
        <router-link :to="`users/${actor.id}/edit`" class="dropdown-item">Edit Profile</router-link>
      </div>
      <div class="dropdown-divider"/>
      <router-link class="dropdown-item" to="/login" @click.native="logOut()">Logout</router-link>
    </div>

  </div>
</template>

<script>
import {Images} from "@/Api";

export default {
  name: "UserProfileLinks",
  computed: {
    /**
     * Current actor
     * Returns {name, id, type}
     **/
    actor() {
      return this.$root.$data.user.state.actingAs
    },

    /** Returns the current logged in users data **/
    actorName() {
      return this.actor.name
    },

    /** A list of user accounts the user can change to.
     * Empty list if the user is already acting as themselves **/
    userAccounts() {
      return [this.$root.$data.user.state.userData]
    },

    /** A list of the users associated business accounts **/
    businessAccounts() {
      return this.$root.$data.user.state.userData.businessesAdministered
    },

    /**
     * Returns if the acting as user is a GAA
     */
    isGAA() {
      return this.$root.$data.user.isGAA()
    },

    /**
     * Returns if the acting as user is a DGAA
     */
    isDGAA() {
      return this.$root.$data.user.isDGAA()
    },

    isActingAsUser() {
      return this.$root.$data.user.isActingAsUser()
    }
  },
  methods: {
    /**
     * Uses the primaryImageId of the user to find the primary image and return its imageURL,
     * else it returns the default user image url
     */
    getPrimaryImageThumbnail(userType, currImages, currPrimaryImageId) {
      let images = currImages
      let primaryImageId = currPrimaryImageId
      if (images === undefined) {
        if (this.actor.type === 'user') {
          const userThumbnailDetails = this.getUserPrimaryImageDetails(images, primaryImageId)
          images = userThumbnailDetails.images
          primaryImageId = userThumbnailDetails.primaryImageId
        }
        if (this.actor.type === 'business') {
          const businessThumbnailDetails = this.getBusinessPrimaryImageDetails(images, primaryImageId)
          images = businessThumbnailDetails.images
          primaryImageId = businessThumbnailDetails.primaryImageId
        }
      }
      if (primaryImageId != null && images != null) {
        const filteredImages = images.filter(function (specificImage) {
          return specificImage.id === primaryImageId;
        })
        if (filteredImages.length === 1) {
          return this.getImageURL(filteredImages[0].thumbnailFilename)
        }
      }
      if (userType === 'business') {
        return this.getImageURL('/media/defaults/defaultBusinessProfile_thumbnail.jpg')
      }
      return this.getImageURL('/media/defaults/defaultProfile_thumbnail.jpg')
    },

    /**
     * Gets a user's list of images and their primaryImageId
     */
    getUserPrimaryImageDetails(currImages, currPrimaryImageId) {
      let images = currImages
      let primaryImageId = currPrimaryImageId
      for (const user of this.userAccounts) {
        if (this.actor.id === user.id) {
          images = user.images
          primaryImageId = user.primaryImageId
          break
        }
      }
      return {images, primaryImageId}
    },

    /**
     * Gets a business's list of images and their primaryImageId
     */
    getBusinessPrimaryImageDetails(currImages, currPrimaryImageId) {
      let images = currImages
      let primaryImageId = currPrimaryImageId
      for (const business of this.businessAccounts) {
        if (this.actor.id === business.id) {
          images = business.images
          primaryImageId = business.primaryImageId
          break
        }
      }
      return {images, primaryImageId}
    },

    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
    },
    /** Logs the user out **/
    logOut() {
      this.$root.$data.user.logout();
    },

    /** Sets the current logged in user to act as a business account **/
    async actAsBusiness(business) {
      await this.$root.$data.user.setActingAs(business.id, business.name, 'business')
      this.$router.push({name: 'home'})
    },

    /** Sets the current logged in user to act as a user account **/
    async actAsUser(userData) {
      await this.$root.$data.user.setActingAs(userData.id, userData.firstName + ' ' + userData.lastName, 'user')
      this.$router.push({name: 'home'})
    }
  }
}
</script>

<style scoped>

.profile-image {
  height: 35px;
  margin-right: 5px;
}

.profile-image-sm {
  height: 20px;
  margin-right: 5px;
}

.dropdown-item {
  display: flex;
  align-items: center;
}

.admin-badge {
  margin-left: 10px;
}

</style>