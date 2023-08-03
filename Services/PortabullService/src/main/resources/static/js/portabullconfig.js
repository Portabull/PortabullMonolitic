
var windowLocationHref_B = window.location.href;
var urlHref_B94857 = new URL(windowLocationHref_B);

const BASE_URL = urlHref_B94857.protocol + "//" + urlHref_B94857.hostname  + ":" + urlHref_B94857.port + "/APIGateway/";
const gmailUrl =urlHref_B94857.protocol + "//" + urlHref_B94857.hostname  + ":" + urlHref_B94857.port +  "2029/oauth/sign-in-with-gmail";


const pageRedirectionPort = "pageRedirectionPort";