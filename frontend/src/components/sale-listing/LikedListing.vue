<!--
LikedListing.vue
Displays a user's liked listings.

@prop listingData: The json data (from the api) to display
-->
<template>
  <div>

    <div class="card shadow card-size">

      <!-- Listing Image -->
      <img v-if="imageUrl != null" :src="imageUrl" alt="productImage" class="card-img-top">

      <div class="card-body">

        <!-- Product Name -->
        <em v-if="tagColour" id="tag" class="bi bi-tag-fill float-right" :style="cssVars"/>
        <h6 class="card-title">{{ data.listing.inventoryItem.product.name }}</h6>

        <!-- Quantity and Price, cause sizing issues -->
        <p class="card-text text-muted small mb-1">
          Quantity: {{ data.listing.quantity }}
        </p>

        <p class="card-text text-muted small mb-1">
          Price: {{ formatPrice(data.listing) }}
        </p>

        <div class="text-right">

          <button class="btn btn-sm btn-outline-primary"><em class="bi bi-tag"/> Tag</button>

          <!-- Open Listing Modal -->
          <button
              class="btn btn-sm btn-outline-primary ml-3"
              data-target="#viewListingModal"
              data-toggle="modal"
              @click="viewListing(data.listing)"
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
            <button aria-label="Close" class="close" data-dismiss="modal" type="button" @click="closeModal">
              <span aria-hidden="true">&times;</span>
            </button>
            <individual-sale-listing-modal :listing="listingToView"
                                           @viewBusiness="viewBusiness"></individual-sale-listing-modal>
          </div>
        </div>
      </div>
    </div>

    <div v-if="viewBusinessModal" id="viewBusinessModal" class="modal fade" data-backdrop="static">
      <div class="modal-dialog modal-xl">
        <div class="modal-content">
          <div class="modal-body">
            <button aria-label="Close" class="close" data-dismiss="modal" type="button"
                    @click="viewBusinessModal=false">
              <span aria-hidden="true">&times;</span>
            </button>
            <business-profile-page-modal :id="businessToViewId"></business-profile-page-modal>
          </div>
        </div>
      </div>
    </div>

  </div>

</template>

<script>
import {Images} from "@/Api";
import IndividualSaleListingModal from "@/components/sale-listing/IndividualSaleListingModal";
import BusinessProfilePageModal from "@/components/business/BusinessProfilePageModal"

// const tagColours = {
//   RED: "red",
//   ORANGE: "orange",
//   YELLOW: "yellow",
//   GREEN: "green",
//   BLUE: "blue",
//   PURPLE: "purple",
//   NONE: null
// }

export default {
  name: "LikedListing",
  components: {
    IndividualSaleListingModal,
    BusinessProfilePageModal
  },
  props: {
    // Data of the sale listing.
    data: {
      type: Object,
      required: true
    },
  },

  data() {
    return {
      listingToView: null,
      viewListingModal: false,
      viewBusinessModal: false,
      businessToViewId: null,
      imageUrl: null
    }
  },

  mounted() {
    this.getPrimaryImage(this.data.listing.inventoryItem.product)
  },

  computed: {
    /**
     * Returns the colour associated with a tag
     */
    tagColour() {
      return "purple"
      //return tagColours[this.data.tag]
    },

    cssVars() {
      return {
        '--colour': this.tagColour
      }
    }
  },

  methods: {
    /**
     * Method called after closing the modal
     */
    closeModal() {
      this.viewListingModal = false
      this.$emit('updateData')
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
    },

    /**
     * Turns popup modal to view  business on
     * @param listing the listing object for the modal to show
     */
    viewListing(listing) {
      this.viewBusinessModal = false
      this.listingToView = listing
      this.viewListingModal = true
    },

    /**
     * Turns popup modal to view  business on
     * @param listing the listing object with the business information to show
     */
    viewBusiness(listing) {
      this.viewListingModal = false
      this.businessToViewId = listing.business.id
      this.viewBusinessModal = true
    }
  }
}
</script>

<style scoped>

#tag {
  color: var(--colour);
  font-size: 30px;
  transition: 0.3s;
}

#tag:hover {
  text-shadow: var(--colour) 0 0 3px;
}

.card-size {
  margin-bottom: 40px;
}

.card-img-top {
  object-fit: cover;
  max-height: 200px;
}

</style>