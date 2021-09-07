<!--
PopularListing.vue
Displays a popular listing

@prop listingData: The json data (from the api) to display
-->
<template>
  <div>
    <div class="card shadow card-size">

      <!-- Listing Image -->
      <img v-if="imageUrl != null" :src="imageUrl" alt="productImage" class="card-img-top">

      <div class="card-body">
        <!-- Product Name -->
        <h6 class="card-title">{{ data.listing.inventoryItem.product.name }}</h6>

        <!-- Quantity and Price, cause sizing issues -->
        <p class="card-text text-muted small mb-1">
          Quantity: {{ data.listing.quantity }}
        </p>

        <p class="card-text text-muted small mb-1">
          Price: {{ formatPrice(data.listing) }}
        </p>

        <div class="text-right">
          <!-- Open Listing Modal -->
          <button
              class="btn btn-sm btn-outline-primary ml-3"
              data-target="#viewListingModal"
              data-toggle="modal"
              @click="viewListingModal = true"
          >
            View Details
          </button>

        </div>
      </div>
    </div>

    <individual-sale-listing-modal v-if="viewListingModal" :listing="data.listing"
                                   @close-modal="closeModal"
    />
  </div>
</template>

<script>

import {Images} from "@/Api";
import IndividualSaleListingModal from "@/components/sale-listing/IndividualSaleListingModal";
export default {
  name: "PopularListing",
  components: {
    IndividualSaleListingModal,
  },
  props: {
    // Data of the sale listing.
    data: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      viewListingModal: false,
      businessToViewId: null,
      imageUrl: null
    }
  },
  mounted() {
    this.getPrimaryImage(this.data.listing.inventoryItem.product)
  },

  methods: {
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
      return this.$root.$data.product.formatPrice(listing.currency, listing.price);
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