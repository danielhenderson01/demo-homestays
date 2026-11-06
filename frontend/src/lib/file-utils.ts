import { fileTypeFromBlob } from "file-type";

const allowedMimeByExt: Record<string, string[]> = {
  png: ["image/png"],
  jpg: ["image/jpeg"],
  jpeg: ["image/jpeg"],
  webp: ["image/webp"],
  heic: ["image/heic", "image/heif"],
  heif: ["image/heif"],
  avif: ["image/avif"],
  bmp: ["image/bmp"],
  tiff: ["image/tiff", "image/x-tiff"],
};

const allowedExts = [
  "png",
  "jpg",
  "jpeg",
  "webp",
  "heic",
  "heif",
  "avif",
  "bmp",
  "tiff",
];

export async function validateFile(file: File) {
  const res = await fileTypeFromBlob(file);

  if (!res) {
    throw new Error("Invalid file type");
  }

  const { ext, mime } = res;

  if (!allowedExts.includes(ext)) {
    throw new Error("Extension not allowed");
  }

  const allowedMimes = allowedMimeByExt[ext] ?? [];

  if (!mime || !allowedMimes.includes(mime)) {
    throw new Error(`MIME not allowed for ${ext}`);
  }

  return { validFile: file, validExt: ext, validMime: mime };
}
