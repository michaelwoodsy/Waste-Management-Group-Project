<template>
  <div class="container-fluid">

    <!-- Page title -->
    <div class="row mb-4">
      <div class="col text-center">
        <h2>{{ PageTitle }}</h2>
      </div>
    </div>

    <!-- Form fields -->
    <div>
      <!-- Section -->
      <div class="form-group row">
        <label for="section"><strong>Section<span class="required">*</span></strong></label>
          <select id="section" v-model="section" :class="{'form-control': true, 'is-invalid': msg.section}" >
            <option value="ForSale">
              For Sale
            </option>
            <option value="Wanted">
              Wanted
            </option>
            <option value="Exchange">
              Exchange
            </option>
          </select>
        <span class="invalid-feedback">{{ msg.section }}</span>
      </div>

      <!-- Title -->
      <div class="form-group row">
        <label for="title"><strong>Card Title<span class="required">*</span></strong></label>
        <input id="title" v-model="title" :class="{'form-control': true, 'is-invalid': msg.title}"
               placeholder="Enter the title"
               required maxlength="255" type="text">
        <span class="invalid-feedback">{{ msg.title }}</span>
      </div>

      <!-- Description -->
      <div class="form-group row">
        <label for="description"><strong>Description</strong></label>
        <input id="description" v-model="description" :class="{'form-control': true, 'is-invalid': false}"
               placeholder="Enter the description"
               required maxlength="255" type="text">
      </div>

      <!-- Keywords -->
      <div class="form-group row">
        <label for="keywords">
          <strong>Keywords<span class="required">*</span></strong>
           (Separate keywords with a comma no spaces)
        </label>
        <KeywordInput  />
        <input id="keywords" v-model="keywords" :class="{'form-control': true, 'is-invalid': msg.keywords}"
               placeholder="Enter the Keywords"
               required maxlength="255" type="text">
        <span class="invalid-feedback">{{ msg.keywords }}</span>
      </div>

      <!-- Create Card button -->
      <div class="form-group row mb-0">
        <div class="btn-group" style="width: 100%">
          <button id="cancelButton" ref="close" class="btn btn-secondary col-4" data-dismiss="modal" v-on:click="cancel=true" @click="close">Cancel</button>
          <button id="createButton" class="btn btn-primary col-8" v-on:click="submit=true" @click="checkInputs">Create Card</button>
        </div>
        <!-- Show an error if required fields are missing -->
        <div v-if="msg.errorChecks" class="error-box">
          <alert class="mb-0">{{ msg.errorChecks }}</alert>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import Alert from "@/components/Alert";
import KeywordInput from "@/components/marketplace/KeywordInput";

export default {
  name: "CreateCardPage",
  components: {
    Alert,
    KeywordInput
  },
  data() {
    return {
      PageTitle: 'Create a new card',
      creatorId: '', //Required
      section: '', //Required
      title: '', //Required
      description: '',
      keywords: '',
      msg: {
        section: null,
        title: null,
        keywords: null,
        errorChecks: null
      },
      valid: true,
      cancel: false,
      submit: false
    };
  },
  methods: {
    /**
     * Validate the section input
     * Must select one of the 3 available sections
     */
    validateSection(){
      if (this.section === '' || this.section === null){
        this.msg.section = 'Please select a section'
        this.valid = false
      } else if (this.section === 'ForSale' || this.section === 'Wanted' || this.section === 'Exchange'){
        this.msg.section = null
      } else {
        this.msg.section = 'Please select an appropriate section'
        this.valid = false
      }
    },
    /**
     * Validate the title input
     * Cannot be empty
     */
    validateTitle(){
      if (this.title === '' || this.title === null){
        this.msg.title = 'Please enter a title'
        this.valid = false
      } else {
        this.msg.title = null
      }
    },

    /**
     * Validate the keywords input
     * Will edit keywords to be correct, remove spaces (Comma separated string FOR NOW)
     */
    validateKeywords(){
      if (this.keywords === '' || this.keywords === null){
        this.msg.keywords = 'Please enter one or more keywords'
        this.valid = false
      } else if(this.keywords.includes(', ') || this.keywords.includes(' ')){
        //Remove the leading and trailing white spaces
        this.keywords = this.keywords.replace(/^\s+|\s+$/g, '')
        //Remove any multiple commas
        this.keywords = this.keywords.replace(/,+/g, ',')
        //Remove white spaces after comma
        this.keywords = this.keywords.replace(/,\s+/g, ',')
        //Replace white spaces between words with hyphens
        this.keywords = this.keywords.replace(/\s/g, '-')
        this.msg.keywords = null
      }else {
        this.msg.keywords = null
      }
    },

    /**
     * Check all inputs are valid, if not show error message otherwise add card to marketplace
     */
    checkInputs(){
      this.validateSection()
      this.validateTitle()
      this.validateKeywords()

      if (!this.valid) {
        this.msg.errorChecks = 'Please fix the shown errors and try again';
        console.log(this.msg.errorChecks);
        this.valid = true;
      } else {
        this.msg.errorChecks = null;
        console.log('No errors');
        this.addCard()
      }
    },

    /**
     * Add a new card to the marketplace
     */
    addCard() {
      this.$root.$data.user.createCard(
          {
            "creatorId": this.$root.$data.user.state.actingAs.id,
            "section": this.section,
            "title": this.title,
            "description": this.description,
            "keywords": this.keywords
          }
      ).then(() => {
        this.$refs.close.click();
        this.close();
      }).catch((err) => {
        this.msg.errorChecks = err.response
            ? err.response.data.slice(err.response.data.indexOf(":") + 2)
            : err
      });
    },
    /**
     * Closes the popup window to create a card
     */
    close() {
      this.$emit('refresh-cards');
    }
  }
}
</script>

<style scoped>

.required {
  color: red;
}

.form-group {
  margin-bottom: 30px;
}

.error-box {
  width: 100%;
  margin: 20px;
  text-align: center;
}

</style>