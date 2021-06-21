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
        <label for="keywordValue">
          <strong>Keywords<span class="required">*</span></strong>
          (Keywords must be 25 characters or less)
        </label>
        <input id="keywordValue" v-model="keywordValue"
               :class="{'form-control': true, 'is-invalid': msg.keywords}"
               placeholder="Enter the Keywords"
               required maxlength="25" type="text"
               style="margin-bottom: 2px"
               autocomplete="off"
               data-toggle="dropdown"
               @input="filterKeywords"
               @keyup.space="addKeyword"/>
        <div class="dropdown-menu overflow-auto" id="dropdown">
          <p class="text-muted dropdown-item left-padding mb-0 disabled"
             v-if="keywordValue.length == 0"
          >
            Start typing...
          </p>

          <p class="text-muted dropdown-item left-padding mb-0 disabled"
             v-else-if="filteredKeywords.length === 0 && keywordValue.length > 0"
          >
            No results found.
          </p>

          <a class="dropdown-item pointer left-padding"
             v-for="keyword in filteredKeywords"
             v-else
             :key="keyword"
             @click="setKeyword(keyword)">
            <span>{{ keyword }}</span>
          </a>
        </div>
        <div class="keyword" v-for="(keyword, index) in keywords" style="padding: 2px"
             :key="'keyword' + index">
          <button class="btn btn-primary">
            <span>{{  keyword  }}</span>
            <span @click="removeKeyword(index)"><em class="bi bi-x"></em></span>
          </button>
        </div>
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

export default {
  name: "CreateCardPage",
  components: {
    Alert,
  },
  data() {
    return {
      PageTitle: 'Create a new card',
      creatorId: '', //Required
      section: '', //Required
      title: '', //Required
      description: '',
      msg: {
        section: null,
        title: null,
        keywords: null,
        errorChecks: null
      },
      valid: true,
      cancel: false,
      submit: false,
      keywordValue: '',
      keywords: [],
      testKeywords: [
          'Fun',
          'Party',
          'Cars',
          'Fortnite',
          'Apples',
          'Bananas',
          'Test'
      ],
      filteredKeywords: []
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
      if (this.keywords.length === 0){
        this.msg.keywords = 'Please enter one or more keywords'
        this.valid = false
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
    },

    /**
    * Adds a keyword to the list of keywords
    */
    addKeyword() {
      this.keywordValue = this.keywordValue.trim()
      if(!(this.keywordValue === '' || this.keywordValue === ' ') && !this.keywords.includes(this.keywordValue)) {
        this.keywords.push(this.keywordValue);
      }
      this.keywordValue = '';
    },

    /**
     * Removes a keyword from the list of keywords
     * @param index Index of the keyword in the keyword list
     */
    removeKeyword(index) {
      this.keywords.splice(index, 1)
    },

    filterKeywords() {
      if (this.keywordValue.length > 0) {
        this.filteredKeywords = this.testKeywords.filter(keywordValue => {
          return keywordValue.toLowerCase().startsWith(this.keywordValue.toLowerCase())
        })
      } else {
        this.filteredKeywords = []
      }
    },

    setKeyword(keyword) {
      this.keywordValue = keyword
      this.addKeyword()
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