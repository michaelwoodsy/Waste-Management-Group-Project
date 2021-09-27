<template>
  <div class="card shadow">

    <div class="card-header d-flex align-items-center">
      <img :src="profileImage" alt="profile image" class="profile-image rounded-circle"/>
      <span class="ml-1">{{ review.user.firstName }} {{ review.user.lastName }}</span>
    </div>

    <div class="card-body">

      <div class="text-left mb-2">
        <h6 class="card-title mb-0">{{ review.sale.productName }}</h6>
      </div>

      <div class="text-left d-flex align-items-center mb-2">
        <em
            v-for="val in ratingList"
            :key="val"
            :class="{'bi-star': val > review.rating, 'bi-star-fill': val <= review.rating}"
            class="icon bi"
        />
        <span class="text-muted ml-2">{{ new Date(review.created).toDateString() }}</span>
      </div>

      <div v-if="review.reviewMessage !== ''" class="text-left">
        {{ review.reviewMessage }}
      </div>

    </div>

  </div>
</template>

<script>
import {formatDate} from "@/utils/dateTime";
import {Images} from "@/Api";

export default {
  name: "Review",
  props: {
    review: Object
  },
  data() {
    return {
      ratingList: [1, 2, 3, 4, 5],
      profileImage: null
    }
  },
  mounted() {
    this.getProfileImage(this.review.user)
  },
  computed: {
    /**
     * Returns formatted created date
     *
     * @returns {string} String representation of date formatted
     */
    formattedDate() {
      return formatDate(this.review.created)
    }
  },
  methods: {
    /**
     * Gets the profile image of a user
     *
     * @param user user to get image for
     */
    getProfileImage(user) {
      let primaryImage;
      for (const image of user.images) {
        if (image.id === user.primaryImageId) {
          primaryImage = image;
        }
      }
      if (primaryImage) {
        this.profileImage = Images.getImageURL(primaryImage.thumbnailFilename)
      } else {
        this.profileImage = Images.getImageURL('/media/defaults/defaultProfile_thumbnail.jpg')
      }
    }
  }
}
</script>

<style scoped>

.profile-image {
  height: 35px;
  margin-right: 5px;
}

.icon {
  color: gold;
  font-size: 20px;
}

</style>