<!--
LikedListing.vue
Displays a user's liked listings.

@prop listingData: The json data (from the api) to display
-->
<template>
  <div>

    <div class="card shadow card-size">

      <!-- Listing Image -->
      <img :src="getPrimaryImageThumbnail(listingData.inventoryItem.product)" alt="productImage"
           class="ui-icon-image listing-image">

      <div class="card-body">

        <!-- Product Name -->
        <h6 class="card-title d-inline"> {{ listingData.inventoryItem.product.name }} </h6>

        <!-- Quantity and Price, cause sizing issues
        <p class="card-text text-muted small mb-1">
          Quantity: {{ listingData.quantity }}
        </p>

        <p class="card-text text-muted small mb-1">
          Price: {{ formatPrice(listingData) }}
        </p>
         -->

        <div class="text-right">

          <!-- Open Listing Modal -->
          <button
              class="btn btn-sm btn-outline-primary ml-5"
              data-target="#viewListingModal"
              data-toggle="modal"
              @click="viewListing(listingData)"
          >
            View Details
          </button>

        </div>
      </div>
    </div>

    <div v-if="viewListingModal" id="viewListingModal" class="modal fade" data-backdrop="static">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-body">
            <button aria-label="Close" class="close" data-dismiss="modal" type="button" @click="viewListingModal=false">
              <span aria-hidden="true">&times;</span>
            </button>
            <individual-sale-listing-modal :listing="listingToView"></individual-sale-listing-modal>
          </div>
        </div>
      </div>
    </div>

  </div>

</template>

<script>
import {Images} from "@/Api";
import IndividualSaleListingModal from "@/components/sale-listing/IndividualSaleListingModal";

export default {
  name: "LikedListing",
  components: {
    IndividualSaleListingModal
  },
  props: {
    // Data of the sale listing.
    listingData: {
      type: Object,
      required: true
    },
  },

  data() {
    return {
      listingToView: null,
      viewListingModal: false,
    }
  },

  methods: {
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
    getPrimaryImageThumbnail(product) {
      if (product.primaryImageId !== null) {
        const filteredImages = product.images.filter(function (specificImage) {
          return specificImage.id === product.primaryImageId;
        })
        if (filteredImages.length === 1) {
          return this.getImageURL(filteredImages[0].thumbnailFilename)
        }
      }
      //Return the default image if the program gets to this point (if it does something went wrong)
      return this.getImageURL('/media/defaults/defaultProduct_thumbnail.jpg')
    },

    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
    },

    /**
     * Turns popup modal to view  business on
     * @param listing the listing object for the modal to show
     */
    viewListing(listing) {
      this.listingToView = listing
      this.viewListingModal = true
    }
  }
}
</script>

<style scoped>
.card-size {
  max-height: 400px;
  max-width: 400px;
  margin-bottom: 40px;
}
</style>