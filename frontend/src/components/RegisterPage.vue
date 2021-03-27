<template>

  <div class="container-fluid">
    <br><br>
    <div class="row">
      <div class="col-12 text-center mb-2">
        <h2>Register for an Account</h2>
      </div>
    </div>
    <logout-required>
      <div class="row">
        <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">

          <div class="form-row">
              <!--    First Name    -->
              <label for="firstName" style="margin-top:20px"><b>First Name<span class="required">*</span></b></label><br/>
              <input style="width:100%" type="text" placeholder="Enter your First Name" id="firstName" class="form-control" v-model="firstName" required><br>
            <!--    Error message for the first name input    -->
              <span class="error-msg" style="margin: 0" v-if="msg.firstName">{{msg.firstName}}</span><br><br>
          </div>

          <div class="form-row">
            <!--    Middle Name    -->
            <label for="middleName"><b>Middle Name</b></label><br/>
            <input style="width:100%" type="text" placeholder="Enter your Middle Name" id="middleName" class="form-control" v-model="middleName"><br><br><br>
          </div>

          <div class="form-row">
            <!--    Last Name    -->
            <label for="lastName"><b>Last Name<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="text" placeholder="Enter your Last Name" id="lastName" class="form-control" v-model="lastName" required><br>
            <!--    Error message for the last name input    -->
            <span class="error-msg" style="margin: 0" v-if="msg.lastName">{{msg.lastName}}</span><br><br>
          </div>

          <div class="form-row">
            <!--    Nickname    -->
            <label for="nickname"><b>Nickname</b></label><br/>
            <input style="width: 100%" type="text" placeholder="Enter your Nickname" id="nickname" class="form-control" v-model="nickname"><br><br><br>
          </div>

          <div class="form-row">
            <!--    Email    -->
            <label for="email"><b>Email<span class="required">*</span></b></label><br/>
            <input style="width: 100%" type="email" placeholder="Enter your Email" id="email" class="form-control" v-model="email" required><br>
            <!--    Error message for the email input    -->
            <span class="error-msg" v-if="msg.email">{{msg.email}}</span><br><br>
          </div>

          <div class="form-row">
            <!--    bio    -->
            <label for="bio" class="bio-label"><b>Bio</b></label><br>
            <textarea placeholder="Write a Bio" id="bio" v-model="bio" class="form-control" style="width: 100%; height: 200px;"></textarea>
          </div><br>

          <div class="form-row">
            <!--    Date of Birth    -->
            <label for="dateOfBirth"><b>Date of Birth<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="date" id="dateOfBirth" class="form-control" v-model="dateOfBirth" required><br>
            <!--    Error message for the date of birth input    -->
            <span class="error-msg" v-if="msg.dateOfBirth">{{msg.dateOfBirth}}</span><br><br>
          </div>

          <div class="form-row">
            <!--    Phone Number    -->
            <label for="phoneNumber"><b>Phone Number</b></label><br/>
            <input style="width:100%" type="text" placeholder="Enter your Phone Number with extension" id="phoneNumber" class="form-control" v-model="phoneNumber"><br><br><br>
          </div>

          <hr/>

          <!--    Title for Address inputs    -->
          <div class="form-row">
            <span class="addressText">Address</span>
          </div>

          <div class="form-row">
            <!--    Home Address Street Number    -->
            <label for="homeAddressNumber"><b>Street Number<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="text" placeholder="Enter your Street Number" id="homeAddressNumber" class="form-control" v-model="address.streetNumber" required><br>
          </div><br>

          <div class="form-row">
            <!--    Home Address Street Name    -->
            <label for="homeAddressStreet"><b>Street Name<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="text" placeholder="Enter your Street Name" id="homeAddressStreet" class="form-control" v-model="address.streetName" required><br>
          </div><br>

          <div class="form-row">
            <!--    Home Address City    -->
            <label for="homeAddressCity"><b>City or Town<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="search" placeholder="Enter your City" id="homeAddressCity" class="form-control" v-model="addressCity" required><br>

            <!--    Autofill City/Town    -->
            <div style="width:100%; text-align: left" v-for="city in cities" v-bind:key="city">
              <a class="address-output" @click="changeCity(city)">{{city}}</a><br>
            </div>
          </div><br>

          <div class="form-row">
            <!--    Home Address Region    -->
            <label for="homeAddressRegion"><b>Region<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="search" placeholder="Enter your Region" id="homeAddressRegion" class="form-control" v-model="addressRegion" required><br>

            <!--    Autofill region    -->
            <div style="width:100%; text-align: left" v-for="region in regions" v-bind:key="region">
              <a class="address-output" @click="changeRegion(region)">{{region}}</a><br>
            </div>
          </div><br>

          <div class="form-row">
            <!--    Home Address Country    -->
            <label for="homeAddressCountry"><b>Country<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="search" placeholder="Enter your Country" id="homeAddressCountry" class="form-control" v-model="addressCountry" required><br>

            <!--    Autofill country    -->
            <div style="width:100%; text-align: left" v-for="country in countries" v-bind:key="country">
              <a class="address-output" @click="changeCountry(country)">{{country}}</a><br>
            </div>
          </div><br>

          <div class="form-row">
            <!--    Home Address Post Code    -->
            <label for="homeAddressPostCode"><b>Postcode<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="text" placeholder="Enter your Postcode" id="homeAddressPostCode" class="form-control" v-model="address.postcode" required><br>
          </div><br>

          <!--    Error message for the address inputs    -->
          <div class="form-row">
            <span class="error-msg" v-if="msg.homeAddress">{{msg.homeAddress}}</span>
          </div>

          <hr/>

          <div class="form-row">
            <!--    Password    -->
            <label for="password"><b>Password<span class="required">*</span></b></label><br>
            <input style="width:100%" type="password" placeholder="Enter your Password" id="password" class="form-control" v-model="password"><br>
            <!--    Error message for the password input    -->
            <span class="error-msg" v-if="msg.password">{{msg.password}}</span><br><br><br>
          </div>

          <div class="form-row">
            <button class="btn btn-block btn-primary" style="width: 100%; margin:0 20px" v-on:click="checkInputs">Create Account</button>
            <!--    Error message for the registering process    -->
            <p style="width: 100%; margin:0 20px; text-align: center"><span class="error-msg" v-if="msg.errorChecks">{{msg.errorChecks}}</span></p><br>
            <p  style="width: 100%; margin:0 20px; text-align: center">Already have an account?
              <router-link class="link-text" to="/login">Login here</router-link></p><br><br>
          </div>
        </div>
      </div>
    </logout-required>
  </div>

</template>

<script>
import axios from "axios";
import LogoutRequired from "./LogoutRequired";

/**
 * Default starting parameters
 */
export default {
  name: "RegisterPage",
  components: {LogoutRequired},
  data() {
    return {
      //Sets text boxes to empty at start
      firstName: '',    //Required
      lastName: '',     //Required
      middleName: '',
      nickname: '',
      bio: '',
      email: '',        //Required
      dateOfBirth: '',  //Required
      phoneNumber: '',

      //Need these to be able to use the watcher methods
      addressCountry: '',
      addressRegion: '',
      addressCity: '',

      address: {  //Required
        streetNumber: '',
        streetName: '',
        city: '',
        region: '',
        country: '',
        postcode: '',
      },

      password: '',
      msg: {
        'firstName': '',
        'lastName': '',
        'email': '',
        'dateOfBirth': '',
        'homeAddress': '',
        'password': '',
        'errorChecks': ''
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
   * these methods are called when their respective input field is changed
   */
  watch: {
    /**
     * Called when the addressCountry variable is updated.
     * cant be when the address.country variable is updated as it cant check a variable in an object
     * Checks if the country can be autofilled, and if so, calls the proton function which returns autofill candidates
     */
    addressCountry(value) {
      this.address.country = value
      //re enable autofill
      if (!this.autofillCountry && this.address.country !== this.prevAutofilledCountry) {
        this.prevAutofilledCountry = ''
        this.autofillCountry = true
      }

      //Only autofill address if the number of characters typed is more than 3
      if (this.autofillCountry && this.address.country.length > 3) {
        this.countries = this.photon(value, 'place:country')
      }
    },

    /**
     * Called when the addressRegion variable is updated.
     * cant be when the address.region variable is updated as it cant check a variable in an object
     * Checks if the region can be autofilled, and if so, calls the proton function which returns autofill candidates
     */
    addressRegion(value) {
      this.address.region = value
      //re enable autofill
      if (!this.autofillRegion && this.address.region !== this.prevAutofilledRegion) {
        this.prevAutofilledRegion = ''
        this.autofillRegion = true
      }

      //Only autofill address if the number of characters typed is more than 3
      if (this.autofillRegion && this.address.region.length > 3) {
        this.regions = this.photon(value, 'boundary:administrative')
      }
    },

    /**
     * Called when the addressCity variable is updated.
     * cant be when the address.city variable is updated as it cant check a variable in an object
     * Checks if the city can be autofilled, and if so, calls the proton function which returns autofill candidates
     */
    addressCity(value) {
      this.address.city = value
      //re enable autofill
      if (!this.autofillCity && this.address.city !== this.prevAutofilledCity) {
        this.prevAutofilledCity = ''
        this.autofillCity = true
      }

      //Only autofill address if the number of characters typed is more than 3
      if (this.autofillCity && this.address.city.length > 3) {
        this.cities = this.photon(value, 'place:city&osm_tag=place:town')
      }
    },
  },

  /**
   * Methods that can be called but the program
   */
  methods: {
    /**
     * Validates the first name variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateFirstName() {
      if (this.firstName === '') {
        this.msg['firstName'] = 'Please enter a First Name'
        this.valid = false
      } else {
        this.msg['firstName'] = ''
      }
    },
    /**
     * Validates the last name variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateLastName() {
      if (this.lastName === '') {
        this.msg['lastName'] = 'Please enter a Last Name'
        this.valid = false
      } else {
        this.msg['lastName'] = ''
      }
    },
    /**
     * Validates the email variable
     * Checks if the string is of an email format using regex, if not, displays a warning message
     */
    validateEmail() {
      if (/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(this.email)) {
        this.msg['email'] = ''
      } else {
        this.msg['email'] = 'Invalid Email Address'
        this.valid = false
      }
    },
    /**
     * Validates the date of birth variable
     * Checks if the string is empty, if so displays a warning message
     */
    validateDateOfBirth() {
      if (this.dateOfBirth === '') {
        this.msg['dateOfBirth'] = 'Please enter a Date of Birth'
        this.valid = false
      } else {
        this.msg['dateOfBirth'] = ''
      }
    },
    /**
     * Validates the address variables
     * Checks if the variables are empty, if so displays a warning message
     */
    validateAddress() {
      if (this.address.streetNumber === '' || this.address.streetName === '' ||
          this.address.city === '' || this.address.region === '' ||
          this.address.country === '' || this.address.postcode === '') {
        this.msg['homeAddress'] = 'Please enter a full Address'
        this.valid = false
      } else {
        this.msg['homeAddress'] = ''
      }
    },
    /**
     * Validates the password variable
     * Checks if the string is empty, if so displays a warning message
     */
    validatePassword() {
      if (this.password === '') {
        this.msg['password'] = 'Please enter a Password'
        this.valid = false
      } else {
        this.msg['password'] = ''
      }
    },

    /**
     * Validating to check if the data entered is inputted correctly, If not displays a warning message
     */
    checkInputs() {
      this.validateFirstName();
      this.validateLastName();
      this.validateEmail();
      this.validateDateOfBirth();
      this.validateAddress();
      this.validatePassword();

      if (!this.valid) {
        this.msg['errorChecks'] = 'Please fix the shown errors and try again';
        console.log('Please fix the shown errors and try again');
        this.valid = true;//Reset the value
      } else {
        this.msg['errorChecks'] = '';
        console.log('No Errors');
        //Send to server here
        this.addUser();
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
            //console.log(addresses)
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
      this.address.country = country
      this.addressCountry = country
      this.countries = []
      this.autofillCountry = false
      this.prevAutofilledCountry = this.address.country
    },

    /**
     * Changes the address.region variable to display the new autofilled region
     * Called when a user clicks a region to autofill
     * @param region autofill string that was chosen
     */
    changeRegion(region) {
      //Changes the address input to the selected autofill address
      this.address.region = region
      this.addressRegion = region
      this.regions = []
      this.autofillRegion = false
      this.prevAutofilledRegion = this.address.region
    },

    /**
     * Changes the address.city variable to display the new autofilled city
     * Called when a user clicks a city to autofill
     * @param city autofill string that was chosen
     */
    changeCity(city) {
      //Changes the address input to the selected autofill address
      this.address.city = city
      this.addressCity = city
      this.cities = []
      this.autofillCity = false
      this.prevAutofilledCity = this.address.city
    },


    /**
     * Add the new user to the server
     * Calls the api function createNew which sends a new user to the backend server
     * If this fails the program should set the error text to the error recived from the backend server
     */
    addUser() {
      console.log(this.phoneNumber)
      console.log(typeof this.phoneNumber)
      this.$root.$data.user.register(
          this.firstName,
          this.lastName,
          this.middleName,
          this.nickname,
          this.bio,
          this.email,
          this.dateOfBirth,
          this.phoneNumber,
          //For now address is string. Will be changed when the database accepts the address object
          `${this.address.streetNumber} ${this.address.streetName}, ${this.address.city}, ${this.address.region}, ${this.address.country}, ${this.address.postcode}`,
          //this.address,
          this.password
      ).then(() => {
                this.$router.push({name: 'user'})
              })
              .catch((err) => {
                this.msg.errorChecks = err;
              });
    },
  }
};

</script>

<style scoped>

.link-text {
  color: blue;
  cursor: pointer;
  margin-right: 10px;
}

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
