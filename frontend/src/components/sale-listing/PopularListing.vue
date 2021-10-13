<!--
PopularListing.vue
Displays a popular listing

@prop listingData: The json data (from the api) to display
-->
<template>
  <div>
    <div class="card shadow card-size" style="width: 15rem; height: 24rem">
      <!-- Listing Image -->
      <img :src="getPrimaryImage(data.inventoryItem.product)" alt="productImage" class="card-img-top">

      <div class="card-body">
        <!-- Product Name -->
        <h6 class="card-title">{{ name }}</h6>

        <!-- Quantity and Price, cause sizing issues -->
        <p class="card-text text-muted small mb-1">
          Quantity: {{ data.quantity }}
        </p>

        <p class="card-text text-muted small mb-1">
          Price: {{ formatPrice(data) }}
        </p>
        <p>
          <em class="bi bi-heart" style="color: red"/> {{ data.likes }}
        </p>
        <div style="position: absolute; bottom: 10px; width: 198px">
          <!-- Open Listing Modal -->
          <button
              class="btn btn-sm btn-outline-primary"
              data-target="#viewListingModal"
              data-toggle="modal"
              @click="viewListingModal = true"
          >
            View Details
          </button>
        </div>

      </div>
    </div>

    <individual-sale-listing-modal v-if="viewListingModal" :listing="data" @close-modal="closeModal"/>
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
      name: ""
    }
  },
  mounted() {
    this.getPrimaryImage(this.data.inventoryItem.product)
    this.formatTitle(this.data.inventoryItem.product.name)
  },

  methods: {
    /**
     * Method that cleans up and shortens the name of the listing
     */
    formatTitle(name){
      if(name.length > 40){
        name = name.slice(0, 37)
        name += "..."
        this.name = name;
      } else {
        this.name = name
      }
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
      if (product.primaryImageId === null) {
        return this.getImageURL('/media/defaults/defaultProduct.jpg')
      } else {
        const filteredImages = product.images.filter(function (specificImage) {
          return specificImage.id === product.primaryImageId;
        })
        if (filteredImages.length === 1) {
          return this.getImageURL(filteredImages[0].filename)
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

.card-size {
  margin-bottom: 40px;
}

.card-img-top {
  object-fit: cover;
  max-height: 190px;
  min-height: 190px;
}
</style>