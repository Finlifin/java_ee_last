import os
import numpy as np
import torch
from PIL import Image
import open_clip
from pymilvus import MilvusClient, DataType, Collection, CollectionSchema, FieldSchema

model, _, preprocess = open_clip.create_model_and_transforms('ViT-B-32', pretrained='laion2b_s34b_b79k')
# model.eval()  # model in train mode by default, impacts some models with BatchNorm or stochastic depth active
client = MilvusClient('images.db')
if client.has_collection(collection_name="images"):
    client.drop_collection(collection_name="images")

client.create_collection(
    collection_name = 'images',
    dimension = 512,
    auto_id = True,
)

def insert_image(images_dir, filename, subject):
    with torch.no_grad():
        image = preprocess(Image.open('%s/%s'%( images_dir, filename ))).unsqueeze(0)
        image_features = model.encode_image(image).to('cpu')
        image_features /= image_features.norm(dim=-1, keepdim=True)
        
        image_features = image_features.numpy().flatten()
        data = { "filename": filename, "subject": subject, "vector": image_features }
        res = client.insert(collection_name='images', data=data)
        

def search_image(image_path, limit = 5):
    # with torch.no_grad(), torch.cuda.amp.autocast():
    with torch.no_grad():
        image = preprocess(Image.open(image_path)).unsqueeze(0)
        image_features = model.encode_image(image).to('cpu')
        image_features /= image_features.norm(dim=-1, keepdim=True)

        image_features = image_features.numpy().flatten()
        result = client.search(
            collection_name = 'images',
            data = [image_features],
            limit = limit,
            search_params = { "params": {} },
            output_fields = ['filename', 'subject'],
        )    
        return result
