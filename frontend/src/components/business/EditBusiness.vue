<!-- Page for editing a business -->
<template>
  <page-wrapper>

    <login-required
        v-if="!isLoggedIn"
        page="Edit a Business"
    />

    <admin-required
        v-else-if="!isAdminOf"
        page="edit this business"
    />

    <div v-else> <!--   If Logged In and Admin   -->
      <div class="row">
        <div class="col-12 text-center my-3">
          <h2>Edit Your Business</h2>
        </div>
      </div>
      <div class="row">

        <div class="col-12 col-sm-8 col-lg-6 col-xl-4 offset-sm-2 offset-lg-3 offset-xl-4 text-center mb-2">

          <div class="form-row">
            <!--    Business Name    -->
            <label for="businessName" style="margin-top:20px">
              <strong>Business Name<span class="required">*</span></strong>
            </label>
            <br/>
            <input id="businessName" v-model="businessName" class="form-control"
                   maxlength="200" placeholder="Enter your Business Name"
                   required style="width:100%" type="text">
            <br>
            <!--    Error message for business name input   -->
            <span v-if="msg.businessName" class="error-msg" style="margin: 0">{{ msg.businessName }}</span>
            <br>
            <br>
          </div>
          <!--    Primary Admin    -->
          <!-- Only a DGAA/GAA or the primary admin themselves can see this-->
          <div class="form-row" v-if="isPrimaryAdmin">
            <label for="businessType">
              <strong>Primary Administrator<span class="required">*</span></strong>
            </label>
            <br/>
            <select id="primaryAdmin" v-model="primaryAdmin" class="form-control"
                    required style="width:100%" type="text">
              <option v-for="admin in administrators"
                      :key="admin.id"
                      :value="admin"
              >
                {{admin.firstName}} {{admin.lastName}}
              </option>
            </select>
            <br>
          </div>

          <div class="form-row">
            <!--    Business Description    -->
            <label class="description-label" for="description">
              <strong>Bio</strong>
            </label>
            <br>
            <textarea id="description" v-model="description" class="form-control"
                      maxlength="255" placeholder="Write a Business Description (Max length 255 characters)"
                      style="width: 100%; height: 200px;">

            </textarea>
          </div>
          <br>

          <hr/>
          <address-input-fields
              ref="addressInput"
              :showErrors="submitClicked"
              @setAddress="(newAddress) => {this.address = newAddress}"
              @setAddressValid="(isValid) => {this.addressIsValid = isValid}"
          />
          <hr/>

          <div class="form-row">
            <!--    Business Type    -->
            <label for="businessType">
              <strong>Business Type<span class="required">*</span></strong>
            </label>
            <br/>
            <select id="businessType" v-model="businessType" class="form-control"
                    required style="width:100%" type="text">
              <option disabled hidden selected value>Please select one</option>
              <option>Accommodation and Food Services</option>
              <option>Retail Trade</option>
              <option>Charitable organisation</option>
              <option>Non-profit organisation</option>
            </select>
            <br>

            <!--    Error message for business type input   -->
            <span v-if="msg.businessType" class="error-msg" style="margin: 0">{{ msg.businessType }}</span>
            <br>
            <br>
          </div>

          <div class="form-row">
            <button class="btn btn-block btn-primary m-2"
                    v-on:click="checkInputs">
              Save Changes
            </button>

            <!--TODO: add a cancel button? -->
            <!--TODO: Interface to edit images -->

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

  </page-wrapper>
</template>

<script>
import LoginRequired from "@/components/LoginRequired";
import Alert from "../Alert"
import AddressInputFields from "@/components/AddressInputFields";
import PageWrapper from "@/components/PageWrapper";
import {Business} from "@/Api";
import AdminRequired from "@/components/AdminRequired";


export default {
  name: "EditBusiness",

  components: {
    PageWrapper,
    AddressInputFields,
    LoginRequired,
    AdminRequired,
    Alert
  },

  data() {
    return {
      businessName: '',
      //isPrimaryAdmin is computed before the prefillFields() method runs
      //So need to have a random number for primaryAdminId so that page loads, then the
      //Primary Administrator section will show/hide based on the actual primary admin
      primaryAdminId: 20, //Id of the current primary admin
      primaryAdmin: null, //Field for editing
      administrators: [],
      description: '',
      address: {
        streetNumber: '',
        streetName: '',
        city: '',
        region: '',
        country: '',
        postcode: '',
      },
      addressIsValid: false,
      businessType: '',

      msg: {
        'businessName': '',
        'description': '',
        'streetName': '',
        'country': '',
        'businessType': '',
        'errorChecks': null
      },
      valid: true,
      submitClicked: 0
    }
  },
  async mounted() {
    await this.prefillFields();
  },

  /**
   * These methods are called when the page opens
   * */
  computed: {
    /**
     * Checks to see if user is logged in currently
     * @returns {boolean|*} true if user is logged in, otherwise false
     */
    isLoggedIn() {
      return this.$root.$data.user.state.loggedIn
    },

    actor() {
      return this.$root.$data.user.state.actingAs;
    },

    /**
     * Check if the user is an admin of the business and is acting as that business, or is a GAA
     */
    isAdminOf() {
      if (this.$root.$data.user.canDoAdminAction()) return true
      else if (this.actor.type !== "business") return false
      return this.actor.id === parseInt(this.$route.params.businessId);
    },

    /**
     * Computes if the current user is the primary admin
     * @returns {boolean|*}
     */
    isPrimaryAdmin() {
      return this.$root.$data.user.canDoAdminAction() ||
          Number(this.$root.$data.user.state.userId) === this.primaryAdminId
    },
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
      this.address.country = value
      //re enable autofill
      if (!this.autofillCountry && this.address.country !== this.prevAutofilledCountry) {
        this.prevAutofilledCountry = ''
        this.autofillCountry = true
      }

      //Cancel Previous axios request if there are any
      this.cancelRequest && this.cancelRequest("User entered more characters into country field")
      //Only autofill address if the number of characters typed is more than 3
      if (this.autofillCountry && this.address.country.length > 3) {
        this.countries = this.photon(value, 'place:country')
      }
    },

    /**
     * Called when the addressRegion variable is updated.
     * cant be when the business.region variable is updated as it cant check a variable in an object
     * Checks if the region can be autofilled, and if so, calls the proton function which returns autofill candidates
     */
    addressRegion(value) {
      this.address.region = value
      //re enable autofill
      if (!this.autofillRegion && this.address.region !== this.prevAutofilledRegion) {
        this.prevAutofilledRegion = ''
        this.autofillRegion = true
      }

      //Cancel Previous axios request if there are any
      this.cancelRequest && this.cancelRequest("User entered more characters into region field")
      //Only autofill address if the number of characters typed is more than 3
      if (this.autofillRegion && this.address.region.length > 3) {
        this.regions = this.photon(value, 'boundary:administrative')
      }
    },

    /**
     * Called when the addressCity variable is updated.
     * cant be when the business.city variable is updated as it cant check a variable in an object
     * Checks if the city can be autofilled, and if so, calls the proton function which returns autofill candidates
     */
    addressCity(value) {
      this.address.city = value
      //re enable autofill
      if (!this.autofillCity && this.address.city !== this.prevAutofilledCity) {
        this.prevAutofilledCity = ''
        this.autofillCity = true
      }
      //Cancel Previous axios request if there are any
      this.cancelRequest && this.cancelRequest("User entered more characters into city field")
      //Only autofill address if the number of characters typed is more than 3
      if (this.autofillCity && this.address.city.length > 3) {
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
     * Validates the address
     * Sets valid = false if the address is not valid
     */
    async validateAddress() {
      if (!this.addressIsValid) {
        this.valid = false
        return
      }
      if (!await this.$refs.addressInput.checkAddressCountry()) {
        this.valid = false
      }
    },

    /**
     * Validating to check if the data entered is input correctly
     * If not an error message is displayed
     */
    async checkInputs() {
      this.submitClicked++;
      this.validateBusinessName();
      await this.validateAddress();
      this.validateBusinessType();

      if (!this.valid) {
        this.msg['errorChecks'] = 'Please fix the shown errors and try again';
        console.log('Please fix the shown errors and try again');
        this.valid = true;//Reset the value
      } else {
        this.msg['errorChecks'] = '';
        console.log('No Errors');
        //Send to server here
        this.editBusiness();
      }
    },

    /**
     * Saves the changes from editing the business by calling the backend endpoint
     * If this fails the program should set the error text to the error received from the backend server
     */
    editBusiness() {
      //TODO: implement me!
    },

    /**
     * Prefills the fields with their existing values
     */
    async prefillFields() {
      const response = await Business.getBusinessData(this.$route.params.businessId)
      this.businessName = response.data.name
      this.description = response.data.description
      this.$refs.addressInput.fullAddressMode = false
      this.$refs.addressInput.address = response.data.address
      this.businessType = response.data.businessType
      this.administrators = response.data.administrators
      this.primaryAdminId = response.data.primaryAdministratorId //Used for computing isPrimaryAdmin

      //Prefill the primary admin dropdown
      for(const admin of this.administrators) {
        if (admin.id === this.primaryAdminId) {
          this.primaryAdmin = admin

        }
      }
    }
  }
}

</script>

<style scoped>

.error-msg {
  color: red;
}

.required {
  color: red;
  display: inline;
}

</style>