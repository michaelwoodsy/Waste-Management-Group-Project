<template>
  <page-wrapper>
    <div class="row mb-3">
      <div class="col">
        <div class="row">
          <div class="col-12 d-flex justify-content-center">
            <h2>
              <strong>{{ listing.inventoryItem.product.name }}</strong>
              <em class="bi mx-2 pointer text-warning"
                  :class="{'bi-star-fill': stared, 'bi-star': !stared}"
                  @click="starListing"
              />
              <em class="bi heart ml-2 pointer"
                  :class="{'bi-heart-fill':liked, 'bi-heart':!liked}"
                  @click="likeListing"/>
              {{ likes }}
            </h2>
          </div>
        </div>

        <!-- Listing images -->
        <div class="modal-body">
          <div v-if="listing.inventoryItem.product.images.length === 0">
            <p class="text-center"><strong>This Product has no Images</strong></p>
          </div>
          <div v-else class="row">
            <div class="col col-12 justify-content-center">
              <div id="imageCarousel" class="carousel slide" data-ride="carousel">
                <div class="carousel-inner">
                  <div v-for="(image, index) in listing.inventoryItem.product.images" v-bind:key="image.id"
                       :class="{'carousel-item': true, 'active': index === 0}">
                    <img class="d-block img-fluid rounded mx-auto w-auto" style="max-height: 500px" :src="getImageURL(image.filename)" alt="ProductImage">
                  </div>
                </div>
                <a class="carousel-control-prev" href="#imageCarousel" role="button" data-slide="prev">
                  <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                  <span class="sr-only">Previous</span>
                </a>
                <a class="carousel-control-next" href="#imageCarousel" role="button" data-slide="next">
                  <span class="carousel-control-next-icon" aria-hidden="true"></span>
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
        <div class="row">
          <div class="col-12 d-flex justify-content-center">
            <button id="buyButton"
                    class="btn btn-primary m-3 buy-button"
                    v-if="!buyClicked"
                    @click="buy"
            >
              Buy
            </button>
            <button v-else
                    class="btn btn-secondary m-3 buy-button"
            >
              Bought
            </button>
          </div>
        </div>

        <!-- Product info -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Details: </p>
          </div>
          <div class="col-6">
            <p style="word-wrap: break-word; max-width: 70%">{{ listing.moreInfo }}</p>
          </div>
        </div>

        <!-- Quantity -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Quantity: </p>
          </div>
          <div class="col-6">
            <p style="word-wrap: break-word; max-width: 70%">{{ listing.quantity }}</p>
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
            <p style="word-wrap: break-word; max-width: 70%">{{ formatDate(listing.created) }}</p>
          </div>
        </div>

        <!-- Listing closes -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Listing closes: </p>
          </div>
          <div class="col-6">
            <p style="word-wrap: break-word; max-width: 70%">{{ formatDate(listing.closes) }}</p>
          </div>
        </div>

        <!-- Seller -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Seller: </p>
          </div>
          <div class="col-6">
            <div class="row">
              <div class="col-6">
                <p style="word-wrap: break-word; max-width: 70%">{{ formatSeller(listing) }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- View Business button -->
        <div class="row">
          <div class="col-12 d-flex justify-content-center">
            <button class="btn btn-primary" @click="viewBusiness(listing)">View Business</button>
          </div>
        </div>
        <br>

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
            <p style="word-wrap: break-word; max-width: 70%">{{ listing.inventoryItem.manufactured }}</p>
          </div>
        </div>

        <!-- Sell by date-->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Sell By Date: </p>
          </div>
          <div class="col-6">
            <p style="word-wrap: break-word; max-width: 70%">{{ listing.inventoryItem.sellBy }}</p>
          </div>
        </div>

        <!-- Best before date -->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Best Before Date: </p>
          </div>
          <div class="col-6">
            <p style="word-wrap: break-word; max-width: 70%">{{ listing.inventoryItem.bestBefore }}</p>
          </div>
        </div>

        <!-- Expiry date-->
        <div class="row">
          <div class="col-6 text-right font-weight-bold">
            <p>Expiry Date: </p>
          </div>
          <div class="col-6">
            <p style="word-wrap: break-word; max-width: 70%">{{ listing.inventoryItem.expires }}</p>
          </div>
        </div>


      </div>
    </div>

  </page-wrapper>
</template>

<script>
import PageWrapper from "@/components/PageWrapper";
import {Images, Business} from "@/Api";
import Alert from "@/components/Alert"
export default {
  name: "IndividualSaleListingModal",
  components: {
    PageWrapper,
    Alert
  },
  props: {
    listing: Object,
  },
  data() {
    return {
      liked: false,
      stared: false,
      likes: 0,
      buyClicked: false,
      purchaseMsg: null,
      errorMsg: null
    }
  },
  mounted() {
    this.likes = this.$props.listing.likes
    this.liked = this.$props.listing.userLikes
    this.stared = this.$props.listing.userStarred
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
     * Formats the name and address of the business offering the listing
     */
    formatSeller(listing) {
      return `${listing.business.name} (${listing.business.businessType}) from ${this.$root.$data.address.formatAddress(listing.business.address)}`
    },

    /**
     * Likes the displayed listing
     */
    async likeListing() {
      if (this.liked) {
        Business.unlikeListing(this.$props.listing.id).then(() => {
          this.liked = !this.liked
          this.likes -= 1
          this.purchaseMsg = "Successfully unliked Listing"
          this.$emit('updateListings')
        }).catch((err) => {
          this.errorMsg = err.response
              ? err.response.data.slice(err.response.data.indexOf(":") + 2)
              : err
        })
      } else {
        Business.likeListing(this.$props.listing.id).then(() => {
          this.liked = !this.liked
          this.likes += 1
          this.purchaseMsg = "Successfully liked Listing!"
          this.$emit('updateListings')
        }).catch((err) => {
          this.errorMsg = err.response
              ? err.response.data.slice(err.response.data.indexOf(":") + 2)
              : err
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
        this.$emit('updateListings')
      }).catch((err) => {
        this.buyClicked = false
        this.errorMsg = err.response
            ? err.response.data.slice(err.response.data.indexOf(":") + 2)
            : err
      });
    },

    /**
     * Lets user view the business the listing belongs to
     * @param listing
     */
    viewBusiness(listing) {
      this.$emit('viewBusiness', listing)
    },

    /**
     * Stars and un-stars the sale listing.
     */
    starListing() {
      console.log(this.stared)
      this.stared = !this.stared
      Business.starListing(this.listing.id, this.stared)
    }

  }
}
</script>

<style scoped>
.heart {
  color: red
}

.buy-button {
  width: 150px;
}

</style>