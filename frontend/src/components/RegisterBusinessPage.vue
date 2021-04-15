<template>
  <div>

    <login-required
        page="Register a Business"
        v-if="!isLoggedIn"
    />

    <div v-else class="container-fluid"> <!--    If Logged In    -->
        <br><br>

        <div class="row">
          <div class="col-12 text-center mb-2">
            <h2>Register a Business</h2>
          </div>
        </div>
      <div class="row">

        <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">

          <div class="form-row">
            <!--    Business Name    -->
            <label for="businessName" style="margin-top:20px">
              <b>Business Name<span class="required">*</span></b>
            </label>
            <br/>
            <input style="width:100%" type="text"
                   placeholder="Enter your Business Name" id="businessName"
                   class="form-control" v-model="businessName" required>
            <br>
            <!--    Error message for business name input   -->
            <span class="error-msg" style="margin: 0" v-if="msg.businessName">{{msg.businessName}}</span>
            <br>
            <br>
          </div>

          <div class="form-row">
            <!--    Business Description    -->
            <label for="description" class="description-label">
              <b>Bio</b>
            </label>
            <br>
            <textarea placeholder="Write a Business Description" id="description"
                      v-model="description" class="form-control" style="width: 100%; height: 200px;">

            </textarea>
          </div>
          <br>

          <hr/>

          <!--    Title for Address inputs    -->
          <div class="form-row">
            <span class="addressText"><b>Address</b></span>
          </div>

          <div class="form-row">
            <!--    Business Address Street Number    -->
            <label for="businessAddressNumber">
              <b>Street Number</b>
            </label><br/>
            <input style="width:100%" type="text" placeholder="Enter your Street Number"
                   id="businessAddressNumber" class="form-control" v-model="businessAddress.streetNumber">
            <br>
          </div>
          <br>

          <div class="form-row">
            <!--    Business Address Street Name    -->
            <label for="businessAddressStreet">
              <b>Street Name</b>
            </label>
            <br/>
            <input style="width:100%" type="text" placeholder="Enter your Street Name"
                   id="businessAddressStreet" class="form-control" v-model="businessAddress.streetName">
            <br>

            <!--    Error message for street name input   -->
            <span class="error-msg" v-if="msg.streetName">{{msg.streetName}}</span>
          </div><br>

          <div class="form-row">
            <!--    Business Address City    -->
            <label for="businessAddressCity">
              <b>City or Town</b>
            </label>
            <br/>
            <input style="width:100%" type="search" placeholder="Enter your City"
                   id="businessAddressCity" class="form-control" v-model="addressCity">
            <br>

            <!--    Autofill City/Town    -->
            <div style="width:100%; text-align: left" v-for="city in cities" v-bind:key="city">
              <a class="address-output" @click="changeCity(city)">{{city}}</a>
              <br>
            </div>
          </div><br>

          <div class="form-row">
            <!--    Business Address Region    -->
            <label for="businessAddressRegion">
              <b>Region</b>
            </label>
            <br/>
            <input style="width:100%" type="search" placeholder="Enter your Region"
                   id="businessAddressRegion" class="form-control" v-model="addressRegion">
            <br>

            <!--    Autofill region    -->
            <div style="width:100%; text-align: left" v-for="region in regions" v-bind:key="region">
              <a class="address-output" @click="changeRegion(region)">{{region}}</a><br>
            </div>
          </div><br>

          <div class="form-row">
            <!--    Business Address Country    -->
            <label for="businessAddressCountry">
              <b>Country<span class="required">*</span></b>
            </label>
            <br/>
            <input style="width:100%" type="search" placeholder="Enter your Country"
                   id="businessAddressCountry" class="form-control" v-model="addressCountry" required>
            <br>

            <!--    Autofill country    -->
            <div style="width:100%; text-align: left" v-for="country in countries" v-bind:key="country">
              <a class="address-output" @click="changeCountry(country)">{{country}}</a><br>
            </div>
          </div><br>

          <!--    Error message for the country input    -->
          <div class="form-row">
            <span class="error-msg" v-if="msg.country">{{msg.country}}</span>
          </div>

          <div class="form-row">
            <!--    Business Address Post Code    -->
            <label for="businessAddressPostCode">
              <b>Postcode</b>
            </label>
            <br/>
            <input style="width:100%" type="text" placeholder="Enter your Postcode"
                   id="businessAddressPostCode" class="form-control" v-model="businessAddress.postcode">
            <br>
          </div><br>

          <hr/>

          <div class="form-row">
            <!--    Business Type    -->
            <label for="businessType">
              <b>Business Type<span class="required">*</span></b>
            </label>
            <br/>
            <select style="width:100%" type="text" id="businessType"
                    class="form-control" v-model="businessType" required>
              <option hidden disabled selected value>Please select one</option>
              <option>Accommodation and Food Services</option>
              <option>Retail Trade</option>
              <option>Charitable organisation</option>
              <option>Non-profit organisation</option>
            </select>
            <br>

            <!--    Error message for business type input   -->
            <span class="error-msg" style="margin: 0" v-if="msg.businessType">{{msg.businessType}}</span>
            <br>
            <br>
          </div>

          <div class="form-row">
            <button class="btn btn-block btn-primary" style="width: 100%; margin:0 20px"
                    v-on:click="checkInputs">
              Create Business
            </button>
            <!--    Error message for the registering process    -->

            <div class="login-box" style="width: 100%; margin:20px 20px; text-align: center">
              <!-- Show error if something wrong -->
              <alert v-if="msg.errorChecks">
                {{ msg.errorChecks }}
              </alert>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>
import axios from "axios";
import LoginRequired from "@/components/LoginRequired";
import Alert from "./Alert"

/**
 * Default starting parameters
 */
export default {
  name: "RegisterBusinessPage",

  components: {
    LoginRequired,
    Alert
  },

  data() {
    return {
      //Sets text boxes to empty at start
      businessName: '',    //Required
      description: '',
      businessAddress: {  //Required
        streetNumber: '',
        streetName: '',
        city: '',
        region: '',
        country: '',
        postcode: '',
      },
      businessType: '',    //Required

      //Need these to be able to use the watcher methods
      addressCountry: '',
      addressRegion: '',
      addressCity: '',


      msg: {
        'businessName': '',
        'description': '',
        'streetName': '',
        'country': '',
        'businessType': '',
        'errorChecks': null
      },
      valid: true,

      //Used to autofill parts of the address
      countries: [],
      regions: [],
      cities: [],

      //Used to remove autofill when the user clicks an option and enabled again when the user changes the address fields again
      prevAutofilledCountry: '',
      autofillCountry: true,
      prevAutofilledRegion: '',
      autofillRegion: true,
      prevAutofilledCity: '',
      autofillCity: true,
    }
  },
  /**
   * These methods are called when the page opens
   * */
  computed: {
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*} true if user is logged in, otherwise false
     */
    isLoggedIn () {
      return this.$root.$data.user.state.loggedIn
    }
  },

  /**
   * these methods are called when their respective input field is changed
   */
  watch: {
    /**
     * Called when the addressCountry variable is updated.
     * cant be when the business.country variable is updated as it cant check a variable in an object
     * Checks if the country can be autofilled, and if so, calls the proton function which returns autofill candidates
     */
    addressCountry(value) {
      this.businessAddress.country = value
      //re enable autofill
      if (!this.autofillCountry && this.businessAddress.country !== this.prevAutofilledCountry) {
        this.prevAutofilledCountry = ''
        this.autofillCountry = true
      }

      //Only autofill address if the number of characters typed is more than 3
      if (this.autofillCountry && this.businessAddress.country.length > 3) {
        this.countries = this.photon(value, 'place:country')
      }
    },

    /**
     * Called when the addressRegion variable is updated.
     * cant be when the business.region variable is updated as it cant check a variable in an object
     * Checks if the region can be autofilled, and if so, calls the proton function which returns autofill candidates
     */
    addressRegion(value) {
      this.businessAddress.region = value
      //re enable autofill
      if (!this.autofillRegion && this.businessAddress.region !== this.prevAutofilledRegion) {
        this.prevAutofilledRegion = ''
        this.autofillRegion = true
      }

      //Only autofill address if the number of characters typed is more than 3
      if (this.autofillRegion && this.businessAddress.region.length > 3) {
        this.regions = this.photon(value, 'boundary:administrative')
      }
    },

    /**
     * Called when the addressCity variable is updated.
     * cant be when the business.city variable is updated as it cant check a variable in an object
     * Checks if the city can be autofilled, and if so, calls the proton function which returns autofill candidates
     */
    addressCity(value) {
      this.businessAddress.city = value
      //re enable autofill
      if (!this.autofillCity && this.businessAddress.city !== this.prevAutofilledCity) {
        this.prevAutofilledCity = ''
        this.autofillCity = true
      }

      //Only autofill address if the number of characters typed is more than 3
      if (this.autofillCity && this.businessAddress.city.length > 3) {
        this.cities = this.photon(value, 'place:city&osm_tag=place:town')
      }
    },
  },

  /**
   * Methods that can be called by the program
   */
  methods: {

    /**
     * Validates the business name variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateBusinessName() {
      if (this.businessName === '') {
        this.msg['businessName'] = 'Please enter a Business Name'
        this.valid = false
      } else {
        this.msg['businessName'] = ''
      }
    },

    /**
     * Validates the address variables
     * Checks if the variables are empty, if so displays a warning message
     */
    validateBusinessAddress() {
      if (this.businessAddress.country === '') {
        this.msg['country'] = 'Please enter a Country'
        this.valid = false
      } else {
        this.msg['country'] = ''
      }
      if (this.businessAddress.streetNumber !== '' && this.businessAddress.streetName === '') {
        this.msg['streetName'] = 'Please enter a Street Name'
        this.valid = false
      } else {
        this.msg['streetName'] = ''
      }
    },

    /**
     * Validates the business type variable
     * Checks if the selected option is not the default, if so displays a warning message
     */
    validateBusinessType() {
      if (this.businessType === '') {
        this.msg['businessType'] = 'Please select a Business Type'
        this.valid = false
      } else {
        this.msg['businessType'] = ''
      }
    },

    /**
     * Validating to check if the data entered is input correctly
     * If not an error message is displayed
     */
    checkInputs() {
      this.validateBusinessName();
      this.validateBusinessAddress();
      this.validateBusinessType();


      if (!this.valid) {
        this.msg['errorChecks'] = 'Please fix the shown errors and try again';
        console.log('Please fix the shown errors and try again');
        this.valid = true;//Reset the value
      } else {
        this.msg['errorChecks'] = '';
        console.log('No Errors');
        //Send to server here
        this.addBusiness();
      }
    },

    /**
     * Function that uses the photon api to get address data.
     * This function calls the photon api and gets valid address variables, then returns the string of these address variables
     * @param textEntered The text entered into the autofillable text field
     * @param tag The tag used to get a specific part of the address, e.g: Country, Region or City/Town
     * @returns [] address variables that can be autofilled
     */
    photon(textEntered, tag) {

      let addresses = []
      axios.get(`https://photon.komoot.io/api?q=${textEntered}&osm_tag=${tag}&limit=5`)
          .then(function(response) {
            for (let i = 0; i < response.data.features.length; i++) {
              const currAddress = response.data.features[i].properties;
              let addressString = ''
              //Is Country
              if (tag === 'place:country') addressString = `${currAddress.name}`
              //Is Region
              else if (tag === "boundary:administrative") addressString = `${currAddress.name}`
                  //Is City
              //tag is like this so you can get a town or a city
              else if (tag === "place:city&osm_tag=place:town") addressString = `${currAddress.name}`


              //Making sure to not add duplicate addresses as sometimes there is more than one region in the world with the same name
              if (addressString !== '' && addresses.indexOf(addressString) === -1) {
                addresses.push(addressString);
              }

            }
            return addresses
          })
          .catch(function(error){
            console.log(error)
          });
      return addresses
    },

    /**
     * Changes the address.country variable to display the new autofilled country
     * Called when a user clicks a country to autofill
     * @param country autofill string that was chosen
     */
    changeCountry(country) {
      //Changes the address input to the selected autofill address
      this.businessAddress.country = country
      this.addressCountry = country
      this.countries = []
      this.autofillCountry = false
      this.prevAutofilledCountry = this.businessAddress.country
    },

    /**
     * Changes the address.region variable to display the new autofilled region
     * Called when a user clicks a region to autofill
     * @param region autofill string that was chosen
     */
    changeRegion(region) {
      //Changes the address input to the selected autofill address
      this.businessAddress.region = region
      this.addressRegion = region
      this.regions = []
      this.autofillRegion = false
      this.prevAutofilledRegion = this.businessAddress.region
    },

    /**
     * Changes the address.city variable to display the new autofilled city
     * Called when a user clicks a city to autofill
     * @param city autofill string that was chosen
     */
    changeCity(city) {
      //Changes the address input to the selected autofill address
      this.businessAddress.city = city
      this.addressCity = city
      this.cities = []
      this.autofillCity = false
      this.prevAutofilledCity = this.businessAddress.city
    },


    /**
     * Add the new business to the server
     * Calls the api function createNew which sends a new business to the backend server
     * If this fails the program should set the error text to the error received from the backend server
     */
    addBusiness() {
      this.$root.$data.business.register(
          this.$root.$data.user.state.userId,
          this.businessName,
          this.description,
          //For now address is string. Will be changed when the database accepts the address object. Remove the following line when addresses are an object and uncomment the line below
          `${this.businessAddress.streetNumber} ${this.businessAddress.streetName}, ${this.businessAddress.city}, ${this.businessAddress.region}, ${this.businessAddress.country}, ${this.businessAddress.postcode}`,
          //this.homeAddress,
          this.businessType
      ).then(() => {
        this.$router.push({name: 'user'})
      })
          .catch((err) => {
            this.msg.errorChecks = err.response
                ? err.response.data.slice(err.response.data.indexOf(":")+2)
                : err
          });
    },
  }
}

</script>

<style scoped>

.address-output {
  cursor: pointer;
  font-size: 14px;
}

.error-msg {
  color: red;
}

.addressText {
  font-size: 30px;
}

.required {
  color: red;
  display: inline;
}

</style>