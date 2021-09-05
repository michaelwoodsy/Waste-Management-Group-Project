<template>
  <div>

    <h2>Sales Report</h2>

    <sales-report
        :data="report"
    />

  </div>
</template>

<script>
import SalesReport from "@/components/sales-report/SalesReport";
export default {
  name: "SalesReportPage",
  components: {SalesReport},
  data() {
    return {
      report: [
        {
          dateStart: '2021-06-01',
          dateEnd: '2021-06-30',
          total: 50.00,
          totalSales: 4,
          sales: [
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            }
          ]
        },
        {
          dateStart: '2021-07-01',
          dateEnd: '2021-07-31',
          total: 40.00,
          totalSales: 5,
          sales: [
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
          ]
        },
        {
          dateStart: '2021-07-01',
          dateEnd: '2021-07-31',
          total: 40.00,
          totalSales: 5,
          sales: [
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
          ]
        },
        {
          dateStart: '2021-07-01',
          dateEnd: '2021-07-31',
          total: 40.00,
          totalSales: 5,
          sales: [
            {
              dateSold: "2021-09-04",
              productId: "WATT-BEANS",
              productName: "Watties Baked Beans",
              quantity: 6,
              price: 5.50,
              currencyCountry: "New Zealand",
              currency: {}
            },
            {
              dateSold: "2021-09-05",
              productId: "KID-BEANS",
              productName: "Value Kidney Beans",
              quantity: 4,
              price: 3.50,
              currencyCountry: "Australia",
              currency: {}
            },
          ]
        }
      ],
    }
  }
}
</script>

<style scoped>

</style>