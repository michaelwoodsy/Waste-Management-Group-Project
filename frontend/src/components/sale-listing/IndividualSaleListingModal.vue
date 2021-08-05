<template>
  <page-wrapper>
    <div class="row mb-3">
      <div class="col">
        <div class="row">
          <div class="col-12 d-flex justify-content-center">
            <h2><strong>{{ listing.inventoryItem.product.name }}</strong></h2>
            <em :class="{bi:true, 'bi-heart-fill':liked, 'bi-heart':!liked, heart:true}" @click="likeListing"/>
            <h2 style="margin-left: 10px">{{ likes }}</h2>
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

        <!-- Business Button -->
        <div class="row py-1">
          <div class="col text-center">
            <button class="btn btn-primary" @click="viewBusiness(listing)">View Business</button>
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
            <p style="word-wrap: break-word; max-width: 70%">{{ formatSeller(listing) }}</p>
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
import {Images} from "@/Api";
export default {
  name: "IndividualSaleListingModal",
  components: {
    PageWrapper
  },
  props: {
    listing: Object
  },
  data() {
    return {
      //TODO: Set liked status based on user viewing
      liked: false,
      //TODO: Set number of likes based on product
      likes: 0,
      buyClicked: false
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
     * Formats the name and address of the business offering the listing
     */
    formatSeller(listing) {
      return `${listing.business.name} (${listing.business.businessType}) from ${this.$root.$data.address.formatAddress(listing.business.address)}`
    },

    /**
     * Likes the displayed listing
     */
    likeListing() {
      this.liked = !this.liked
      //TODO: Include code to like a listing here (Call backend)

      if (this.liked) this.likes = this.likes + 1
      else this.likes = this.likes - 1
    },

    /**
     * Buy the listing
     */
    buy() {
      this.buyClicked = true

    },

    /**
     * Lets user view the business the listing belongs to
     * @param listing
     */
    viewBusiness(listing) {
      this.$emit('viewBusiness', listing)
    },

  }
}
</script>

<style scoped>

.heart {
  color: red;
  font-size: 30px;
  margin-left: 20px;
}

.buy-button {
  width: 150px;
}

</style>