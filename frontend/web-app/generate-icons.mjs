import sharp from "sharp";

await sharp("public/icons/eComm-logo.png")
  .resize(192, 192)
  .toFile("public/icons/icon-192.png");

await sharp("public/icons/eComm-logo.png")
  .resize(512, 512)
  .toFile("public/icons/icon-512.png");

console.log("Icons generated!");
