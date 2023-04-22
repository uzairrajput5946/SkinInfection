import io
import numpy

from flask import Flask, request, jsonify
from tensorflow.keras.models import load_model
from base64 import b64decode
from Crypto.Cipher import AES
from PIL import Image
from tensorflow.keras.preprocessing.image import img_to_array

app = Flask(__name__)
model = load_model('../model')
classes = ['Carbuncles and Furuncles', 'Cellulitis', 'Folliculitis', 'Impetigo']

@app.route('/', methods=['GET'])
def test():
  return 'Flask server running'

@app.route('/classify_image', methods=['POST'])
def classify_image():
  # Get image from request
  encrypted_image = request.json['image']

  decrypted_image = decrypt_image(encrypted_image)

  predicted_class = predict_class(decrypted_image)
  
  return jsonify({'message': 'Image recieved', 'class': predicted_class})

def decrypt_image(image):
  ciphertext = b64decode(image)

  key = b'0123456789abcdef'
  iv = b'fedcba9876543210'
  cipher = AES.new(key, AES.MODE_CBC, iv)

  plaintext = cipher.decrypt(ciphertext)
  decrypted_image = plaintext.decode()

  return decrypted_image

def predict_class(image_base64):
  image_binary = b64decode(image_base64)
  buffer = io.BytesIO(image_binary)
  image = Image.open(buffer)
  image = image.resize((256, 256))

  x = img_to_array(image)
  x = numpy.expand_dims(x, axis=0)
  result = model.predict(x)
  prediction = classes[numpy.argmax(result)]

  return prediction

if __name__ == '__main__':
  app.run(debug=True, host='0.0.0.0', port=3001)
