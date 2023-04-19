import base64
import io
import numpy as np

from flask import Flask, request, jsonify
from tensorflow.keras.models import load_model
from PIL import Image
from tensorflow.keras.preprocessing.image import img_to_array

app = Flask(__name__)
model = load_model('../model')
classes = ['Carbuncles and Furuncles', 'Cellulitis', 'Folliculitis', 'Impetigo']

@app.route('/test', methods=['GET'])
def test():
  return 'Flask server running'

@app.route('/classify_image', methods=['POST'])
def classify_image():
  # Get image from request
  image = request.json['image']
  image_binary = base64.b64decode(image)
  buffer = io.BytesIO(image_binary)
  img = Image.open(buffer)
  img = img.resize((256, 256))

  x = img_to_array(img)
  x = np.expand_dims(x, axis=0)
  result = model.predict(x)
  prediction = classes[np.argmax(result)]
  
  return jsonify({'message': 'Image recieved', 'class': prediction})

if __name__ == '__main__':
  app.run(debug=True, host='0.0.0.0', port=3001)
