<template>
  <div id="viewListingModal" class="modal fade" data-backdrop="static">
    <div class="modal-dialog modal-lg">
      <business-profile-page-modal v-if="viewingBusiness"
                                   :business="listing.business"
                                   :show-back="true"
                                   @back="viewingBusiness = false"
                                   @close-modal="$emit('close-modal')"
      />
      <div v-else class="modal-content">

        <div class="modal-header">
          <div class="col-2"/>
          <div class="col text-center">
            <h2>
              <strong>{{ listing.inventoryItem.product.name }}</strong>
              <em v-if="isLoggedIn && isActingAsUser" :class="{'bi-heart-fill': liked, 'bi-heart': !liked}" class="bi heart pointer" @click="likeListing"/>
              <em v-else :class="{'bi-heart-fill': liked, 'bi-heart': !liked}" class="bi heart" style="pointer-events: none"/>
              {{ likes }}
            </h2>
          </div>
          <div class="col-2">
            <button aria-label="Close" class="close" data-dismiss="modal" type="button" @click="$emit('close-modal')">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
        </div>

        <div class="modal-body">

          <!-- Listing images -->
          <div class="mb-3">
            <div v-if="listing.inventoryItem.product.images.length === 0">
              <img :src="getImageURL('/media/defaults/defaultProduct.jpg')" alt="ProductImage"
                   class="d-block img-fluid rounded mx-auto w-auto" style="max-height: 300px">
            </div>
            <div v-else class="row">
              <div class="col col-12 justify-content-center">
                <div id="imageCarousel" class="carousel slide" data-ride="carousel">
                  <div class="carousel-inner">
                    <div v-for="(image, index) in listing.inventoryItem.product.images" v-bind:key="image.id"
                         :class="{'carousel-item': true, 'active': index === 0}">
                      <img :src="getImageURL(image.filename)" alt="ProductImage"
                           class="d-block img-fluid rounded mx-auto w-auto" style="max-height: 300px">
                    </div>
                  </div>
                  <a class="carousel-control-prev" data-slide="prev" href="#imageCarousel" role="button">
                    <span aria-hidden="true" class="carousel-control-prev-icon"></span>
                    <span class="sr-only">Previous</span>
                  </a>
                  <a class="carousel-control-next" data-slide="next" href="#imageCarousel" role="button">
                    <span aria-hidden="true" class="carousel-control-next-icon"></span>
                    <span class="sr-only">Next</span>
                  </a>
                </div>
              </div>
            </div>
          </div>

          <!-- Show error if something wrong -->
          <alert v-if="errorMsg" class="text-center">
            {{ errorMsg }}
          </alert>

          <alert v-if="purchaseMsg" alert-type="alert-success" class="text-center">
            {{ purchaseMsg }}
          </alert>

          <!-- Buy button -->
          <div class="row text-center mb-3">
            <div class="col">
              <button v-if="!buyClicked && isLoggedIn && isActingAsUser"
                      id="buyButton"
                      class="btn btn-primary mx-2 button"
                      @click="buy"
              >
                Buy
              </button>
              <button v-else-if="isLoggedIn && isActingAsUser" class="btn btn-outline-secondary mx-2 button" disabled>
                Bought
              </button>
              <button v-if="isLoggedIn" class="btn btn-primary mx-2 button" @click="viewBusiness">View Business</button>
            </div>
          </div>

          <!-- Seller -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Seller: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 90%">
                {{ listing.business.name }}<br>
                <span class="text-muted small">{{ listing.business.businessType }}</span><br>
                <span class="text-muted small">{{ formatAddress(listing.business.address) }}</span>
              </p>
            </div>
          </div>

          <!-- Product info -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Details: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 90%">{{ listing.moreInfo }}</p>
            </div>
          </div>

          <!-- Quantity -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Quantity: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 90%">{{ listing.quantity }}</p>
            </div>
          </div>

          <!-- Price -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Price: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 70%">{{ formatPrice(listing) }}</p>
            </div>
          </div>

          <!-- Listing created -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Listing created: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 90%">{{ formatDate(listing.created) }}</p>
            </div>
          </div>

          <!-- Listing closes -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Listing closes: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 90%">{{ formatDate(listing.closes) }}</p>
            </div>
          </div>

          <!-- Manufacturer -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Manufacturer: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 70%">{{ listing.inventoryItem.product.manufacturer }}</p>
            </div>
          </div>

          <!-- Manufactured date-->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Manufactured Date: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 90%">{{ formatDate(listing.inventoryItem.manufactured) }}</p>
            </div>
          </div>

          <!-- Sell by date-->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Sell By Date: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 90%">{{ formatDate(listing.inventoryItem.sellBy) }}</p>
            </div>
          </div>

          <!-- Best before date -->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Best Before Date: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 90%">{{ formatDate(listing.inventoryItem.bestBefore) }}</p>
            </div>
          </div>

          <!-- Expiry date-->
          <div class="row">
            <div class="col-6 text-right font-weight-bold">
              <p>Expiry Date: </p>
            </div>
            <div class="col-6">
              <p style="word-wrap: break-word; max-width: 90%">{{ formatDate(listing.inventoryItem.expires) }}</p>
            </div>
          </div>

        </div>

      </div>
    </div>
  </div>
</template>

<script>
import {Business, Images} from "@/Api";
import Alert from "@/components/Alert"
import BusinessProfilePageModal from "@/components/business/BusinessProfilePageModal";

export default {
  name: "IndividualSaleListingModal",
  components: {
    BusinessProfilePageModal,
    Alert
  },
  props: {
    listing: Object
  },
  data() {
    return {
      liked: false,
      likes: 0,
      buyClicked: false,
      purchaseMsg: null,
      errorMsg: null,
      viewingBusiness: false
    }
  },
  watch: {
    listing() {
      this.buyClicked = false
      this.purchaseMsg = null
      this.errorMsg = null
      this.likes = this.$props.listing.likes
      this.liked = this.$props.listing.userLikes
    }
  },
  mounted() {
    this.likes = this.$props.listing.likes
    this.liked = this.$props.listing.userLikes
  },
  computed: {
    /**
     * Returns true if the user is logged in, false if they are not
     */
    isLoggedIn() {
      return this.$root.$data.user.isLoggedIn()
    },
    /**
     * Returns true if the user is acting as a user, false if they are not
     */
    isActingAsUser() {
      return this.$root.$data.user.isActingAsUser()
    }
  },
  methods: {
    /**
     * Retrieves the image specified by the path
     */
    getImageURL(path) {
      return Images.getImageURL(path)
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
     * Formats the address of the business offering the listing
     */
    formatAddress(address) {
      return `${this.$root.$data.address.formatAddress(address)}`
    },

    /**
     * Likes the displayed listing
     */
    async likeListing() {
      if (this.liked) {
        Business.unlikeListing(this.$props.listing.id).then(() => {
          this.liked = !this.liked
          this.likes -= 1
          this.$emit('update-listings')
        }).catch((err) => {
          this.showError(err)
        })
      } else {
        Business.likeListing(this.$props.listing.id).then(() => {
          this.liked = !this.liked
          this.likes += 1
          this.$emit('update-listings')
        }).catch((err) => {
          this.showError(err)
        })
      }
    },

    /**
     * Buy the listing
     */
    async buy() {
      this.buyClicked = true
      await Business.purchaseListing(this.listing.id).then(() => {
        this.purchaseMsg = "Successfully purchased Listing!"
        this.$emit('update-listings')
      }).catch((err) => {
        this.buyClicked = false
        this.showError(err)
      });
    },

    /**
     * Lets user view the business the listing belongs to
     */
    viewBusiness() {
      this.viewingBusiness = true
    },

    /**
     * Method that shows an error when needed
     */
    showError(err) {
      this.errorMsg = err.response
          ? err.response.data.slice(err.response.data.indexOf(":") + 2)
          : err
    }

  }
}
</script>

<style scoped>

.heart {
  color: red;
  font-size: 30px;
  margin-left: 20px;
  transition: 0.3s;
}

.heart:hover {
  text-shadow: red 0 0 5px;
}

.button {
  width: 150px;
}

</style>