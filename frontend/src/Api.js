/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

/**
 * Declare all available services here
 */
import axios from 'axios'

const SERVER_URL = process.env.VUE_APP_SERVER_ADD;

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 2000,
  withCredentials: true
});

export default {
  // (C)reate


  // (R)ead
  getAll: () => instance.get('students', {
    transformResponse: [function (data) {
      return data? JSON.parse(data)._embedded.students : data;
    }]
  }),
  // (U)pdate
  updateForId: (id, firstName, lastName) => instance.put('students/'+id, {firstName, lastName}),
  // (D)elete
  removeForId: (id) => instance.delete('students/'+id)
}

const fakeUserResults = [
  {
    "id": 100,
    "firstName": "John",
    "lastName": "Smith",
    "middleName": "Hector",
    "nickname": "Jonny",
    "bio": "Likes long walks on the beach",
    "email": "johnsmith99@gmail.com",
    "dateOfBirth": "1999-04-27",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "4 Rountree Street, Upper Riccarton",
    "created": "2018-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 101,
    "firstName": "Robby",
    "lastName": "Abril",
    "middleName": "Francesco",
    "nickname": "Rob",
    "bio": "Likes dogs",
    "email": "rob@uclive.ac.nz",
    "dateOfBirth": "2002-01-05",
    "phoneNumber": "+64 3 555 8761",
    "homeAddress": "15 Clyde Road, Upper Riccarton",
    "created": "2019-07-14T10:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 102,
    "firstName": "Jacob",
    "lastName": "Curley",
    "middleName": "Darick",
    "nickname": "Jake",
    "bio": "Likes cats",
    "email": "jacob@uclive.ac.nz",
    "dateOfBirth": "2000-03-02",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "18 Willow Street, Wigram",
    "created": "2017-02-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 103,
    "firstName": "James",
    "lastName": "Emmett",
    "middleName": "Mecham",
    "nickname": null,
    "bio": "Likes watching netflix",
    "email": "jame@uclive.ac.nz",
    "dateOfBirth": "2001-02-15",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "89 Tree Street, Upper Riccarton",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 104,
    "firstName": "Nathaniel",
    "lastName": "Cleveland",
    "middleName": null,
    "nickname": "Nathan",
    "bio": null,
    "email": "nathaniel@uclive.ac.nz",
    "dateOfBirth": "1998-02-01",
    "phoneNumber": "+64 3 555 0149",
    "homeAddress": "10 Rock Crescent, Ilam",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 105,
    "firstName": "Son",
    "lastName": "Vansant",
    "middleName": null,
    "nickname": null,
    "bio": "Likes walking",
    "email": "son@uclive.ac.nz",
    "dateOfBirth": "2007-02-12",
    "phoneNumber": "+64 3 555 0981",
    "homeAddress": "1 Ferry Road, Sumner",
    "created": "2014-01-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 106,
    "firstName": "Richard",
    "lastName": "Buenrostro",
    "middleName": "Free",
    "nickname": null,
    "bio": "Likes long walks on the beach",
    "email": "richard@uclive.ac.nz",
    "dateOfBirth": "2001-02-15",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "123 Pear Street, Chirstchurch, New Zealand",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 107,
    "firstName": "Mickey",
    "lastName": "Rankins",
    "middleName": null,
    "nickname": "Mick",
    "bio": "Likes long walks on the beach",
    "email": "mickey@uclive.ac.nz",
    "dateOfBirth": "2001-02-15",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "8 Rountree Street, Upper Riccarton",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 108,
    "firstName": "Emily",
    "lastName": "Melva",
    "middleName": "Gahan",
    "nickname": null,
    "bio": "Likes long walks on the beach",
    "email": "emily@uclive.ac.nz",
    "dateOfBirth": "2001-02-15",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "8 Rountree Street, New Brighton",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 109,
    "firstName": "Gail",
    "lastName": "Meaux",
    "middleName": null,
    "nickname": null,
    "bio": "Likes long walks on the beach",
    "email": "gail@uclive.ac.nz",
    "dateOfBirth": "2001-02-15",
    "phoneNumber": "+64 3 321 0129",
    "homeAddress": "8 Rountree Street, Hornby",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 110,
    "firstName": "Denise",
    "lastName": "Michael",
    "middleName": null,
    "nickname": "Denny",
    "bio": "Likes long walks on the beach",
    "email": "denise@uclive.ac.nz",
    "dateOfBirth": "2001-02-15",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "8 Rountree Street, Sydenham",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 111,
    "firstName": "Eve",
    "lastName": "Maldonado",
    "middleName": "Omega",
    "nickname": null,
    "bio": "Likes long walks on the beach",
    "email": "eve@uclive.ac.nz",
    "dateOfBirth": "2001-02-15",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "8 Rountree Street, Burnside",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 112,
    "firstName": "Beatrice",
    "lastName": "Marte",
    "middleName": "Reta",
    "nickname": null,
    "bio": "Likes long walks on the beach",
    "email": "beatrice@uclive.ac.nz",
    "dateOfBirth": "2001-02-15",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "8 Rountree Street, Fendalton",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  },
  {
    "id": 113,
    "firstName": "Yasuko",
    "lastName": "Wick",
    "middleName": "Marte",
    "nickname": null,
    "bio": "Likes long walks on the beach",
    "email": "yasuko@uclive.ac.nz",
    "dateOfBirth": "2001-02-15",
    "phoneNumber": "+64 3 555 0129",
    "homeAddress": "8 Rountree Street, Addington",
    "created": "2020-07-14T14:32:00Z",
    "role": "user",
    "businessesAdministered": [
      12,
      42,
      99
    ]
  }
];

export const User = {
  createNew: (firstName,
              lastName,
              middleName,
              nickname,
              bio,
              email,
              dateOfBirth,
              phoneNumber,
              homeAddress,
              password) => instance.post('users', {firstName, lastName, middleName, nickname, bio, email, dateOfBirth, phoneNumber, homeAddress, password}),

  login: (username, password) => instance.post('login', {username, password}),

  getUserData: (id) => instance.get(`users/${id}`, {}),

  getUserDataFake (id) {
    return new Promise ((resolve, reject) => {
      this.getUserData(id)
          .then((res) => {
            res.data = fakeUserResults.find(user => user.id === id);
            resolve(res)
          })
          .catch((err) => {
            reject(err)
          })
    })
  },

  getUsers: (searchTerm) => instance.get('users/search', {params: {'searchQuery': searchTerm}}),

  getUsersFake (searchTerm) {
    return new Promise ((resolve, reject) => {
      this.getUsers(searchTerm)
          .then((res) => {
            res.data = [...fakeUserResults];
            resolve(res)
          })
          .catch((err) => {
            reject(err)
          })
    })
  }
};


export const Business = {

  getBusinessData: (id) => instance.get(`businesses/${id}`, {}),

  getProductData: (businessId, productId) => instance.get(`business/${businessId}/products/${productId}`, {})
};