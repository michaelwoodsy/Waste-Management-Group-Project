<template>
  <div class="container-fluid p-0">

    <!--    Result Information    -->
    <div v-if="!this.selectingItem || (!loading && this.products.length > 0)">

      <!-- Displays number of results -->
      <div class="text-center">
        <showing-results-text
            :items-per-page="resultsPerPage"
            :page="page"
            :total-count="totalCount"
        />
      </div>

      <!--    Order By   -->
      <div class="overflow-auto">
        <table class="table table-hover">
          <thead>
          <tr>
            <!--    Product Code    -->
            <th class="pointer" scope="col" @click="orderResults('id')">
              <p class="d-inline">Code</p>
              <p v-if="orderCol === 'id'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--    Product Image    -->
            <th scope="col"></th>

            <!--    Full Name    -->
            <th class="pointer" scope="col" @click="orderResults('name')">
              <p class="d-inline">Product Info</p>
              <p v-if="orderCol === 'name'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--    Manufacturer    -->
            <th class="pointer" scope="col" @click="orderResults('manufacturer')">
              <p class="d-inline">Manufacturer</p>
              <p v-if="orderCol === 'manufacturer'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--    RRP    -->
            <th class="pointer" scope="col" @click="orderResults('recommendedRetailPrice')">
              <p class="d-inline">RRP</p>
              <p v-if="orderCol === 'recommendedRetailPrice'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--    Date Added    -->
            <th class="pointer" scope="col" @click="orderResults('created')">
              <p class="d-inline">Date Added</p>
              <p v-if="orderCol === 'created'" class="d-inline">{{ orderDirArrow }}</p>
            </th>

            <!--    Edit button column    -->
            <th scope="col"></th>
            <!--    view images button column    -->
            <th scope="col"></th>
          </tr>
          </thead>

          <!--    Product Information    -->
          <tbody v-if="!loading">
          <tr v-for="product in paginatedProducts"
              v-bind:key="product.id"
          >
            <th scope="row">{{ product.id }}</th>
            <td>
              <img alt="productImage"
                   :src="getPrimaryImageThumbnail(product)">
            </td>
            <td style="word-break: break-word; width: 40%">
              {{ product.name }}
              <span v-if="product.description" style="font-size: small"><br/>{{ product.description }}</span>
            </td>
            <td>{{ product.manufacturer }}</td>
            <td>{{ formatPrice(product.recommendedRetailPrice) }}</td>
            <td>{{ new Date(product.created).toDateString() }}</td>
            <td v-if="!selectingItem">
              <button class="btn btn-primary" @click="editProduct(product.id)">Edit</button>
            </td>
            <td v-if="!selectingItem">
              <button class="btn btn-primary" data-target="#viewImages" data-toggle="modal"
                      @click="changeViewedProduct(product)">View Images</button>
            </td>
            <td v-if="selectingItem">
              <button class="btn btn-primary" @click="selectProduct(product.id)">Select</button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

    </div>

    <div v-if="loading" class="row">
      <div class="col text-center">
        <p class="text-muted">Loading...</p>
      </div>
    </div>

    <div v-if="!loading && this.products.length === 0 && this.selectingItem" class="text-center">
      You have no products to create an inventory entry for...
    </div>

    <!--    Result Information    -->
    <div class="row">
      <div class="col">
        <pagination
            :current-page.sync="page"
            :items-per-page="resultsPerPage"
            :total-items="totalCount"
            class="mx-auto"
        />
      </div>
    </div>

    <!--   Product images modal   -->
    <div v-if="isViewingImages" id="viewImages" class="modal fade" data-backdrop="static">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{productViewing.name}}'s Images</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close" @click="isViewingImages=false">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <div v-if="productViewing.images.length === 0">
              <p class="text-center"><strong>This Product has no Images</strong></p>
            </div>
            <div v-else class="row" style="height: 500px">
              <div class="col col-12 justify-content-center">
                <div id="imageCarousel" class="carousel slide" data-ride="carousel">
                  <div class="carousel-inner">
                    <div v-for="(image, index) in productViewing.images" v-bind:key="image.id"
                         :class="{'carousel-item': true, 'active': index === 0}">
                      <img class="d-block img-fluid rounded mx-auto d-block" style="height: 500px" :src="getImageURL(image.filename)" alt="ProductImage">
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
        </div>
      </div>
    </div>



  </div>
</template>

<script>
import ShowingResultsText from "../ShowingResultsText";
import Pagination from "../Pagination";
import {Business, Images} from '@/Api';

export default {
  name: "CatalogueItems",
  components: {
    ShowingResultsText,
    Pagination
  },
  props: [
    'businessId',
    'selectingItem'
  ],
  data() {
    return {
      products: [],
      currency: null,
      error: null,
      orderCol: null,
      orderDirection: false, // False -> Ascending
      resultsPerPage: 10,
      page: 1,
      loading: false,
      createNewProduct: false,
      isViewingImages: false,
      productViewing: null,
      image: null
    }
  },
  mounted() {
    this.getCurrencyAndFillTable()
  },
  computed: {
    /**
     * Checks which direction (ascending or descending) the order by should be
     * @returns {string}
     */
    orderDirArrow() {
      if (this.orderDirection) {
        return '↓'
      }
      return '↑'
    },

    /**
     * Sort Products Logic
     * @returns {[]|*[]}
     */
    sortedProducts() {
      if (this.orderCol === null) {
        return this.products
      }

      // Create new products array and sort
      let newProducts = [...this.products];
      // Order direction multiplier for sorting
      const orderDir = (this.orderDirection ? 1 : -1);

      // Sort products if there are any
      if (newProducts.length > 0) {
        // Sort products
        newProducts.sort((a, b) => {
          return orderDir * this.sortAlpha(a, b)
        });
      }

      return newProducts
    },

    /**
     * Paginate the products
     * @returns {*[]|*[]}
     */
    paginatedProducts() {
      let newProducts = this.sortedProducts;

      // Sort products if there are any
      if (newProducts.length > 0) {
        // Splice the results to showing size
        const startIndex = this.resultsPerPage * (this.page - 1);
        const endIndex = this.resultsPerPage * this.page;
        newProducts = newProducts.slice(startIndex, endIndex)
      }

      return newProducts
    },
    /**
     * Calculates the number of results in products array
     * @returns {number}
     */
    totalCount() {
      return this.products.length
    }
  },
  watch: {
    /**
     * Called when the businessId is changed, this occurs when the path variable for the business id is updated
     */
    businessId() {
      this.getCurrencyAndFillTable()
    }
  },
  methods: {
    /**
     * Uses the primaryImageId of the product to find the primary image and return its imageURL,
     * else it returns the default product image url
     */
    getPrimaryImageThumbnail(product) {
      if (product.primaryImageId === null) {
        return this.getImageURL('/media/defaults/defaultProduct_thumbnail.jpg')
      }
      const filteredImages = product.images.filter(function(specificImage) {
        return specificImage.id === product.primaryImageId;
      })
      if (filteredImages.length === 1) {
        return this.getImageURL(filteredImages[0].thumbnailFilename)
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
     * Updates order direction
     * @param col column to be ordered
     */
    orderResults(col) {
      // Remove the ordering if the column is clicked and the arrow is down
      if (this.orderCol === col && this.orderDirection) {
        this.orderCol = null;
        this.orderDirection = false;
        return
      }

      // Updated order direction if the new column is the same as what is currently clicked
      this.orderDirection = this.orderCol === col;
      this.orderCol = col;
    },
    /**
     * Function for sorting a list by orderCol alphabetically
     */
    sortAlpha(a, b) {
      if (a[this.orderCol] === null) {
        return -1
      }
      if (b[this.orderCol] === null) {
        return 1
      }
      if (a[this.orderCol] < b[[this.orderCol]]) {
        return 1;
      }
      if (a[this.orderCol] > b[[this.orderCol]]) {
        return -1;
      }
      return 0;
    },

    /**
     * routes to the edit product page
     * @param id of the product
     */
    editProduct(id) {
      this.$router.push({name: 'editProduct', params: {businessId: this.businessId, productId: id}})
    },

    /**
     * Sets the viewing product in order to view the products images
     */
    changeViewedProduct(product) {
      this.productViewing = product
      this.isViewingImages = true
    },

    selectProduct(id) {
      this.$parent.productCode = id;
      this.$parent.finishSelectItem();
    },

    /**
     * Uses the getCurrency in the product.js module to get the currency of the business,
     * and then call the fill table method
     */
    async getCurrencyAndFillTable() {
      this.loading = true

      //The country variable  will always be an actual country as it is a requirement when creating a business
      //Get Businesses country
      const country = (await Business.getBusinessData(parseInt(this.$route.params.businessId))).data.address.country

      this.currency = await this.$root.$data.product.getCurrency(country)

      this.fillTable()
    },

    /**
     * calls the formatPrice method in the product module to format the products recommended retail price
     */
    formatPrice(price) {
      return this.$root.$data.product.formatPrice(this.currency, price)
    },

    /**
     * Fills the table with Product data
     */
    fillTable() {
      this.products = [];
      this.loading = true;
      this.page = 1;

      Business.getProducts(this.$route.params.businessId)
          .then((res) => {
            this.error = null;
            this.products = res.data;
            this.loading = false;
          })
          .catch((err) => {
            this.error = err;
            this.loading = false;
          })
    },
  }
}
</script>

<style scoped>

.pointer {
  cursor: pointer;
}

</style>