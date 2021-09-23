<!--
SaleListing.vue
Displays a single listing.

@prop listingData: The json data for a sale listing (from the api) to display
-->
<template>

    <div v-if="listing" class="card shadow card-size">

      <!-- Listing Image -->
      <img v-if="imageUrl != null" :src="imageUrl" alt="productImage" class="card-img-top">

      <div class="card-body" style="min-height: 300px">

        <!-- Product Name -->
        <h6 class="card-title">{{ listing.inventoryItem.product.name }}</h6>

        <!-- Quantity and Price, cause sizing issues -->
        <p class="card-text text-muted small mb-1">
          Quantity: {{ listing.quantity }}
        </p>

        <p class="card-text text-muted small mb-1">
          Price: {{ formatPrice(listing) }}
        </p>

        <div class="text-right">
          <!-- Open Listing Modal -->
          <button
              class="btn btn-sm btn-outline-primary ml-3"
              @click="routeToSaleListing"
          >
            View Details
          </button>

          <!-- Remove from featured listings button -->
          <button
              class="btn btn-sm btn-outline-danger ml-3"
              id="removeButton"
              v-if="isAdminOfBusiness"
          >
            <em class="bi bi-dash-lg"></em>
          </button>

        </div>
      </div>
    </div>
</template>

<script>
import {Images, Business} from "@/Api";
import product from "@/store/modules/product"
import user from "@/store/modules/user"

export default {
  name: "SaleListing",
  props: {
    // Data of the sale listing.
    listingData: {
      type: Object,
      required: true
    }
  },

  data() {
    return {
      viewListingModal: false,
      businessToViewId: null,
      imageUrl: null,
      listing: null
    }
  },

  mounted() {
    this.getPrimaryImage(this.listingData.inventoryItem.product)
    this.setCurrency()
  },

  computed: {
    /**
     * Returns true if the current user is an administrator of the business.
     * Also returns true if they are a GAA / DGAA
     */
    isAdminOfBusiness() {
      return user.isActingAsBusiness() &&
          user.actor().id === this.listingData.business.id
    }
  },

  methods: {
    /**
     * Sets the currency on the sale listing
     */
    async setCurrency() {
      let listings = await product
          .addSaleListingCurrencies([{...this.listingData}], this.listingData.business.address.country)
      this.listing = listings[0]
    },

    /**
     * Method called after closing the modal
     */
    closeModal() {
      this.$emit('update-data')
    },

    /**
     * Formats the price of a listing based on
     * the country of the business offering the listing
     */
    formatPrice(listing) {
      return product.formatPrice(listing.currency, listing.price)
    },

    /**
     * Formats the date fields.
     * @param string string representation of the date.
     * @returns {string} the formatted date.
     */
    formatDate(string) {
      if (string === '') {
        return '';
      } else {
        return new Date(string).toDateString();
      }
    },

    /**
     * Formats the name and address of the business offering the listing
     */
    formatSeller(listing) {
      return `${listing.business.name} from ${this.$root.$data.address.formatAddress(listing.business.address)}`
    },

    /**
     * Uses the primaryImageId of the product to find the primary image and return its imageURL,
     * else it returns the default product image url
     */
    getPrimaryImage(product) {
      if (product.primaryImageId !== null) {
        const filteredImages = product.images.filter(function (specificImage) {
          return specificImage.id === product.primaryImageId;
        })
        if (filteredImages.length === 1) {
          this.imageUrl = this.getImageURL(filteredImages[0].filename)
        }
      }
    },

    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
    },

    /**
     * Routes to the sale listing page.
     */
    async routeToSaleListing() {
      await this.$emit('close-modal')
      this.$router.push({
        name: 'browseListings',
        query: {businessId: this.listingData.business.id, listingId: this.listingData.id}
      })
    },

    /**
     * Run when the remove button is clicked.
     * Removes the listing from the businesses featured listings.
     * Emits a 'un-feature-listing' event
     */
    async unFeatureListing() {
      return ''
    }
  }
}
</script>

<style scoped>

.icon {
  font-size: 30px;
  transition: 0.3s;
}

.icon:hover {
  text-shadow: currentColor 0 0 5px;
}

.card-size {
  margin-bottom: 40px;
}

.card-img-top {
  object-fit: cover;
  max-height: 200px;
}

</style>