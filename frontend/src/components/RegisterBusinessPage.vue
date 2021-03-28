<template>
  <div>
    <div class="container-fluid"> <!--    If Logged In    -->
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
            <label for="businessName" style="margin-top:20px"><b>Business Name<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="text" placeholder="Enter your Business Name" id="businessName" class="form-control" v-model="businessName" required><br>
            <span class="error-msg" style="margin: 0" v-if="msg.businessName">{{msg.businessName}}</span><br><br>
          </div>

          <div class="form-row">
            <!--    Description    -->
            <label for="description" class="description-label"><b>Bio</b></label><br>
            <textarea placeholder="Write a Business Description" id="description" v-model="description" class="form-control" style="width: 100%; height: 200px;"></textarea>
          </div><br>

          <div class="form-row">
            <!--    Business Address    -->
            <label for="businessAddress"><b>Business Address<span class="required">*</span></b></label><br/>
            <input style="width:100%" type="search" placeholder="Enter your Business Address" id="businessAddress" class="form-control" v-model="businessAddress" required><br>

            <span class="address-output-label" style="width:100%; text-align: left">Autofillable Addresses:</span>
            <div style="width:100%; text-align: left" v-for="address in addresses" v-bind:key="address">
              <a class="address-output" @click="changeAddress(address)">{{address}}</a><br>
            </div>
          </div>

          <div class="form-row">
            <span class="error-msg" v-if="msg.businessAddress">{{msg.businessAddress}}</span>
          </div><br>

          <div class="form-row">
            <!--    Business Type    -->
            <label for="businessType"><b>Business Type<span class="required">*</span></b></label><br/>
            <select style="width:100%" type="text" id="businessType" class="form-control" v-model="businessType" required>
              <option disabled value="">Please select one</option>
              <option>Accommodation</option>
              <option>Food Service</option>
              <option>Retail Trade</option>
              <option>Charitable Organisation</option>
              <option>Non-Profit Organisation</option>
            </select><br>
            <span class="error-msg" v-if="msg.businessType">{{msg.businessType}}</span><br><br>
          </div>

          <div class="form-row">
            <button class="btn btn-block btn-primary" style="width: 100%; margin:0 20px" v-on:click="checkInputs">Create Business</button>
            <p style="width: 100%; margin:0 20px; text-align: center"><span class="error-msg" v-if="msg.errorChecks">{{msg.errorChecks}}</span></p><br>
          </div>

        </div>
      </div>

    </div>
  </div>

</template>

<script>
// import LoginRequired from "@/components/LoginRequired";

export default {
  name: "RegisterBusinessPage",
  /**components: {
    LoginRequired,
  },
   **/
  data() {
    return {
      //Sets text boxes to empty at start
      businessName: '',    //Required
      description: '',
      businessAddress: '',  //Required
      businessType: '',    //Required
      msg: {
        'businessName': '',
        'description': '',
        'businessAddress': '',
        'businessType': '',
      },
      valid: true,
      addresses: [],
      prevAutofilledAddress: '',
      autofill: true,
    }
  },
  /**
  computed: {
    isLoggedIn () {
      return this.$root.$data.user.state.loggedIn
    },
  }
   **/
}

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