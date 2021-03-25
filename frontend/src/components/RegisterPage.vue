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
            <span class="error-msg" v-if="msg.dateOfBirth">{{msg.dateOfBirth}}</span><br><br>
          </div>

          <div class="form-row">
            <!--    Phone Number    -->
            <label for="phoneNumber"><b>Phone Number</b></label><br/>
            <input style="width:100%" type="number" placeholder="Enter your Phone Number" id="phoneNumber" class="form-control" v-model="phoneNumber"><br><br><br>
          </div>

          <div class="form-row">
            <!--    Home Address    -->
            <label for="homeAddress"><b>Home Address<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="search" placeholder="Enter your Home Address" id="homeAddress" class="form-control" v-model="homeAddress" required><br>

            <span class="address-output-label" style="width:100%; text-align: left">Autofillable Addresses:</span>
            <div style="width:100%; text-align: left" v-for="address in addresses" v-bind:key="address">
              <a class="address-output" @click="changeAddress(address)">{{address}}</a><br>
            </div>
          </div>

          <div class="form-row">
            <span class="error-msg" v-if="msg.homeAddress">{{msg.homeAddress}}</span>
          </div><br>

          <div class="form-row">
            <!--    Password    -->
            <label for="password"><b>Password<span class="required">*</span></b></label><br>
            <input style="width:100%" type="password" placeholder="Enter your Password" id="password" class="form-control" v-model="password"><br>
            <span class="error-msg" v-if="msg.password">{{msg.password}}</span><br><br><br>
          </div>

          <div class="form-row">
            <button class="btn btn-block btn-primary" style="width: 100%; margin:0 20px" v-on:click="checkInputs">Create Account</button>
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
import { User } from '@/Api'
import axios from "axios";
import LogoutRequired from "./LogoutRequired";
//Default starting parameters
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
      homeAddress: '',  //Required
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
      addresses: [],
      prevAutofilledAddress: '',
      autofill: true,
    }
  },

  watch: {
    homeAddress(value) {
      //re enable autofill
      if (!this.autofill && this.homeAddress !== this.prevAutofilledAddress) {
        this.autofill = true;
      }

      //Only autofill address if the number of characters typed is more than 5
      if (this.autofill && this.homeAddress.length > 5) {
        this.addresses = this.photon(value);
      }
      //this.validateAddress();
    },
  },


  methods: {
    changePage (page) {
      this.$emit('changePage', page)
    },

    validateFirstName() {
      if (this.firstName === '') {
        this.msg['firstName'] = 'Please enter a First Name';
        this.valid = false;
      } else {
        this.msg['firstName'] = '';
      }
    },
    validateLastName() {
      if (this.lastName === '') {
        this.msg['lastName'] = 'Please enter a Last Name';
        this.valid = false;
      } else {
        this.msg['lastName'] = '';
      }
    },
    validateEmail() {
      if (/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(this.email)) {
        this.msg['email'] = '';
      } else {
        this.msg['email'] = 'Invalid Email Address';
        this.valid = false;
      }
    },
    validateDateOfBirth() {
      if (this.dateOfBirth === '') {
        this.msg['dateOfBirth'] = 'Please enter a Date of Birth';
        this.valid = false;
      } else {
        this.msg['dateOfBirth'] = '';
      }
    },
    validateAddress() {
      if (this.homeAddress === '') {
        this.msg['homeAddress'] = 'Please enter an Address';
        this.valid = false;
      } else {
        this.msg['homeAddress'] = '';
      }
    },
    validatePassword() {
      if (this.password === '') {
        this.msg['password'] = 'Please enter a Password';
        this.valid = false;
      } else {
        this.msg['password'] = '';
      }
    },

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

    //Method that allows autofill of the address
    photon(textEntered) {
      let addresses = [];
      axios.get('https://photon.komoot.io/api?q=' + textEntered)
          .then(function(response) {
            //The addresses that are building addresses, not cities or districts
            for (let i = 0; i < response.data.features.length; i++) {
              if (response.data.features[i].properties.osm_key === "place" && response.data.features[i].properties.type === "house") {
                const currAddress = response.data.features[i].properties;
                let addressString = `${currAddress.housenumber} ${currAddress.street}, `

                //Making sure no possible undefined variables are added. house number, street and country will not be undefined
                //Only one of city or county is required, to reduce string length
                if (typeof currAddress.city !== "undefined") {
                  addressString += `${currAddress.city}, `;
                } else if (typeof currAddress.county !== "undefined") {
                  addressString += `${currAddress.county}, `;
                }
                if (typeof currAddress.state !== "undefined") {
                  addressString += `${currAddress.state}, `;
                }

                addressString += currAddress.country;

                //Making sure to not add duplicate addresses as sometimes the api has multiples of the same address (why though???)
                if (addresses.indexOf(addressString) === -1) {
                  addresses.push(addressString);
                }
              }
            }
          })
          .catch(function(error){
            console.log(error);
          });
      return addresses;
    },

    changeAddress(text) {
      //Changes the address input to the selected autofill address
      this.homeAddress = text;
      this.addresses = [];
      this.autofill = false;
      this.prevAutofilledAddress = this.homeAddress;
    },



//Add user to the server. need a server to function properly.
    addUser() {
      //This api call fails because there is no server yet to add the user to
      User.createNew(
          this.firstName,
          this.lastName,
          this.middleName,
          this.nickname,
          this.bio,
          this.email,
          this.dateOfBirth,
          this.phoneNumber,
          this.homeAddress,
          this.password).then(() => {
          this.$root.$data.user.login(this.username, this.password)
              .then(() => {
                this.$router.push({name: 'user'})
              })
              .catch((err) => {
                this.msg.errorChecks = err;
              });
        console.log("new user created");
      }).catch((err) => {
        this.$log.debug(err);
        this.msg.errorChecks = "Failed to add user";
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
  font-size: 18px;
}

.address-output-label {
  font-size: 12px;
}

.error-msg {
  color: red;
}

.required {
  color: red;
  display: inline;
}

</style>
