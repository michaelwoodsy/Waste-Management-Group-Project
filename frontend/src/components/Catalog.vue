<template>
    <div>
        <div class="container-fluid" v-if="isLoggedIn">

            <!--    Search Users Header    -->
            <div class="row">
                <div class="col-12 text-center mb-2">
                    <h4>Search Users</h4>
                </div>
            </div>

            <!--    Error Alert    -->
            <div v-if="error" class="row">
                <div class="col-8 offset-2 text-center mb-2">
                    <alert>{{ error }}</alert>
                </div>
            </div>

            <!--    Result Information    -->
            <div class="row">
                <div class="d-none d-lg-block col-lg-1"/>
                <div class="col-12 col-lg-10">
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
                                <!--    ID    -->
                                <th scope="col" class="pointer" @click="orderResults('id')">
                                    <p class="d-inline">Id</p>
                                    <p class="d-inline" v-if="orderCol === 'id'">{{ orderDirArrow }}</p>
                                </th>

                                <!--    First Name    -->
                                <th scope="col" class="pointer" @click="orderResults('firstName')">
                                    <p class="d-inline">Firstname</p>
                                    <p class="d-inline" v-if="orderCol === 'firstName'">{{ orderDirArrow }}</p>
                                </th>

                                <!--    Middle Name    -->
                                <th scope="col" class="pointer" @click="orderResults('middleName')">
                                    <p class="d-inline">Middlename</p>
                                    <p class="d-inline" v-if="orderCol === 'middleName'">{{ orderDirArrow }}</p>
                                </th>

                                <!--    Last Name    -->
                                <th scope="col"  class="pointer" @click="orderResults('lastName')">
                                    <p class="d-inline">Lastname</p>
                                    <p class="d-inline" v-if="orderCol === 'lastName'">{{ orderDirArrow }}</p>
                                </th>

                                <!--    Email    -->
                                <th scope="col" class="pointer" @click="orderResults('email')">
                                    <p class="d-inline">Email</p>
                                    <p class="d-inline" v-if="orderCol === 'email'">{{ orderDirArrow }}</p>
                                </th>

                                <!--    Home Address    -->
                                <th scope="col" class="pointer" @click="orderResults('homeAddress')">
                                    <p class="d-inline">Address</p>
                                    <p class="d-inline" v-if="orderCol === 'homeAddress'">{{ orderDirArrow }}</p>
                                </th>
                            </tr>
                            </thead>
                            <!--    User Information    -->
                            <tbody v-if="!loading">
                                <tr v-bind:key="user.id"
                                    v-for="user in paginatedProducts"
                                    @click="viewProduct(user.id)"
                                    class="pointer"
                                >
                                    <th scope="row">{{ user.id }}</th>
                                    <td>{{ nameAndNickname(user) }}</td>
                                    <td>{{ user.middleName }}</td>
                                    <td>{{ user.lastName }}</td>
                                    <td>{{ user.email }}</td>
                                    <td>{{ formattedAddress(user.homeAddress) }}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="d-none d-lg-block col-lg-1"/>
            </div>

            <div class="row" v-if="loading">
                <div class="col-12 text-center">
                    <p class="text-muted">Loading...</p>
                </div>
            </div>

            <!--    Result Information    -->
            <div class="row">
                <div class="col-12">
                    <pagination
                        :total-items="totalCount"
                        :current-page.sync="page"
                        :items-per-page="resultsPerPage"
                        class="mx-auto"
                    />
                </div>
            </div>
        </div>

        <login-required v-else page="search users"/>
    </div>
</template>

<script>
    import LoginRequired from './LoginRequired'
    import ShowingResultsText from "./ShowingResultsText";
    import Pagination from "./Pagination";
    import Alert from './Alert'
    import {User} from "@/Api";

    export default {
        name: "Catalog",
        components: {
            LoginRequired,
            Alert,
            ShowingResultsText,
            Pagination
        },
        data () {
            return {
                products: [],
                error: null,
                orderCol: null,
                orderDirection: false, // False -> Ascending
                resultsPerPage: 10,
                page: 1,
                loading: false
            }
        },
        mounted: {
          fillTable () {
            this.products = [];
            this.loading = true;
            this.page = 1;

            User.getUsers(this.searchTerm)
                .then((res) => {
                  this.error = null;
                  this.products = res.data;
                  this.loading = false;
                })
                .catch((err) => {
                  this.error = err;
                  this.loading = false;
                })
            }
          },

        computed: {
          /**
           * Gets the business ID
           * @returns {any}
           */
          businessId() {
            return this.$route.params.businessId;
          },

          /**
           * Checks to see if user is logged in currently
           * @returns {boolean|*}
           */
            isLoggedIn () {
                return this.$root.$data.user.state.loggedIn
            },

          /**
           * Checks which direction (ascending or descending) the order by should be
           * @returns {string}
           */
            orderDirArrow () {
                if (this.orderDirection) {
                    return '↓'
                }
                return '↑'
            },

          /**
           * Sort Users Logic
           * @returns {[]|*[]}
           */
            sortedProducts () {
                if (this.orderCol === null) {
                    return this.products
                }

                // Create new users array and sort
                let newProducts = [...this.products];
                // Order direction multiplier for sorting
                const orderDir = (this.orderDirection ? 1 : -1);

                // Sort users if there are any
                if (newProducts.length > 0) {
                    // Sort users
                    newProducts.sort((a, b) => {
                        return orderDir * this.sortAlpha(a, b)
                    });
                }

                return newProducts
            },

          /**
           * Paginate the users
           * @returns {*[]|*[]}
           */
            paginatedProducts () {
                let newProducts = this.sortedProducts;

                // Sort users if there are any
                if (newProducts.length > 0) {
                    // Splice the results to showing size
                    const startIndex = this.resultsPerPage * (this.page - 1);
                    const endIndex = this.resultsPerPage * this.page;
                    newProducts = newProducts.slice(startIndex, endIndex)
                }

                return newProducts
            },
          /**
           * Calculates the number of results in users array
           * @returns {number}
           */
            totalCount () {
                return this.products.length
            }
        },
        methods: {

          /**
           * Updates order direction
           * @param col column to be ordered
           */
            orderResults (col) {
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
            sortAlpha (a, b) {
                if(a[this.orderCol] === null) { return -1 }
                if(b[this.orderCol] === null) { return 1 }
                if(a[this.orderCol] < b[[this.orderCol]]) { return 1; }
                if(a[this.orderCol] > b[[this.orderCol]]) { return -1; }
                return 0;
            },
          /**
           * routes to the individual product page
           * @param id of the product
           */
            viewProduct(id) {
                this.$router.push({name: 'viewProduct', params: {productId: id}})
            },
        }
    }
</script>

<style scoped>
.col-centered {
    margin: 0 auto;
    float: none;
}

.pointer {
    cursor: pointer;
}

</style>
